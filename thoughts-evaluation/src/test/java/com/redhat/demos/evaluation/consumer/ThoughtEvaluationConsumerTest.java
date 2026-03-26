package com.redhat.demos.evaluation.consumer;

import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ThoughtEvaluationConsumerTest {

    @Inject
    ThoughtEvaluationConsumer consumer;

    @InjectMock
    EvaluationService evaluationService;

    @Test
    void testConsumeValidThoughtCreatedEvent() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "This is a positive thought about the future";

        Message<JsonObject> message = createThoughtMessage(thoughtId, thoughtContent, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(evaluationService, times(1)).evaluateThought(idCaptor.capture(), contentCaptor.capture());
        assertEquals(thoughtId, idCaptor.getValue());
        assertEquals(thoughtContent, contentCaptor.getValue());
    }

    @Test
    void testConsumeNullPayload() {
        Message<JsonObject> message = createThoughtMessageWithNullPayload("com.redhat.demos.thoughts.created");

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message).toCompletableFuture().join());

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeNonCreatedEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();
        Message<JsonObject> message = createThoughtMessage(thoughtId, "Some content", "com.redhat.demos.thoughts.updated");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeDeletedEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();
        Message<JsonObject> message = createThoughtMessage(thoughtId, "Some content", "com.redhat.demos.thoughts.deleted");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingThoughtId() {
        JsonObject event = new JsonObject()
            .put("content", "Some content");

        Message<JsonObject> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingContent() {
        UUID thoughtId = UUID.randomUUID();
        JsonObject event = new JsonObject()
            .put("id", thoughtId.toString());

        Message<JsonObject> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEmptyContent() {
        UUID thoughtId = UUID.randomUUID();
        JsonObject event = new JsonObject()
            .put("id", thoughtId.toString())
            .put("content", "   ");

        Message<JsonObject> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEvaluationServiceException() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "Test content";

        Message<JsonObject> message = createThoughtMessage(thoughtId, thoughtContent, "com.redhat.demos.thoughts.created");

        doThrow(new RuntimeException("Evaluation failed")).when(evaluationService)
            .evaluateThought(any(), any());

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message).toCompletableFuture().join());

        verify(evaluationService, times(1)).evaluateThought(thoughtId, thoughtContent);
    }

    @Test
    void testExtractThoughtIdAndContentCorrectly() {
        UUID expectedId = UUID.randomUUID();
        String expectedContent = "Specific thought content for extraction test";

        Message<JsonObject> message = createThoughtMessage(expectedId, expectedContent, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(evaluationService, times(1)).evaluateThought(idCaptor.capture(), contentCaptor.capture());
        assertEquals(expectedId, idCaptor.getValue());
        assertEquals(expectedContent, contentCaptor.getValue());
    }

    @Test
    void testConsumeMultipleEventsSequentially() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();

        Message<JsonObject> message1 = createThoughtMessage(thoughtId1, "First thought", "com.redhat.demos.thoughts.created");
        Message<JsonObject> message2 = createThoughtMessage(thoughtId2, "Second thought", "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message1).toCompletableFuture().join();
        consumer.consumeThoughtEvent(message2).toCompletableFuture().join();

        verify(evaluationService, times(2)).evaluateThought(any(UUID.class), anyString());
    }

    private Message<JsonObject> createThoughtMessage(UUID thoughtId, String content, String eventType) {
        JsonObject event = new JsonObject()
            .put("id", thoughtId.toString())
            .put("content", content)
            .put("author", "Test Author")
            .put("authorBio", "Test Bio")
            .put("status", "IN_REVIEW")
            .put("thumbsUp", 0)
            .put("thumbsDown", 0)
            .put("createdAt", LocalDateTime.now().toString())
            .put("updatedAt", LocalDateTime.now().toString());
        return wrapWithCloudEventMetadata(event, eventType);
    }

    private Message<JsonObject> createThoughtMessageWithNullPayload(String eventType) {
        return wrapWithCloudEventMetadata(null, eventType);
    }

    @SuppressWarnings("unchecked")
    private Message<JsonObject> wrapWithCloudEventMetadata(JsonObject event, String eventType) {
        IncomingCloudEventMetadata<JsonObject> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn(eventType);
        when(ceMetadata.getSource()).thenReturn(URI.create("/thoughts-backend"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());
        if (event != null && event.getString("id") != null) {
            when(ceMetadata.getSubject()).thenReturn(Optional.of(event.getString("id")));
        }
        return Message.of(event, Metadata.of(ceMetadata));
    }
}
