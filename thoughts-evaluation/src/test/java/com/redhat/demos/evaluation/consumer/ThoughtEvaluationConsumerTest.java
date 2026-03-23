package com.redhat.demos.evaluation.consumer;

import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
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

        Message<ThoughtEvent> message = createThoughtMessage(thoughtId, thoughtContent, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(evaluationService, times(1)).evaluateThought(idCaptor.capture(), contentCaptor.capture());
        assertEquals(thoughtId, idCaptor.getValue());
        assertEquals(thoughtContent, contentCaptor.getValue());
    }

    @Test
    void testConsumeNullPayload() {
        Message<ThoughtEvent> message = createThoughtMessageWithNullPayload("com.redhat.demos.thoughts.created");

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message).toCompletableFuture().join());

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeNonCreatedEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();
        Message<ThoughtEvent> message = createThoughtMessage(thoughtId, "Some content", "com.redhat.demos.thoughts.updated");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeDeletedEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();
        Message<ThoughtEvent> message = createThoughtMessage(thoughtId, "Some content", "com.redhat.demos.thoughts.deleted");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingThoughtId() {
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtContent("Some content");

        Message<ThoughtEvent> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingContent() {
        UUID thoughtId = UUID.randomUUID();
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);

        Message<ThoughtEvent> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEmptyContent() {
        UUID thoughtId = UUID.randomUUID();
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setThoughtContent("   ");

        Message<ThoughtEvent> message = wrapWithCloudEventMetadata(event, "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message).toCompletableFuture().join();

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEvaluationServiceException() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "Test content";

        Message<ThoughtEvent> message = createThoughtMessage(thoughtId, thoughtContent, "com.redhat.demos.thoughts.created");

        doThrow(new RuntimeException("Evaluation failed")).when(evaluationService)
            .evaluateThought(any(), any());

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message).toCompletableFuture().join());

        verify(evaluationService, times(1)).evaluateThought(thoughtId, thoughtContent);
    }

    @Test
    void testExtractThoughtIdAndContentCorrectly() {
        UUID expectedId = UUID.randomUUID();
        String expectedContent = "Specific thought content for extraction test";

        Message<ThoughtEvent> message = createThoughtMessage(expectedId, expectedContent, "com.redhat.demos.thoughts.created");

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

        Message<ThoughtEvent> message1 = createThoughtMessage(thoughtId1, "First thought", "com.redhat.demos.thoughts.created");
        Message<ThoughtEvent> message2 = createThoughtMessage(thoughtId2, "Second thought", "com.redhat.demos.thoughts.created");

        consumer.consumeThoughtEvent(message1).toCompletableFuture().join();
        consumer.consumeThoughtEvent(message2).toCompletableFuture().join();

        verify(evaluationService, times(2)).evaluateThought(any(UUID.class), anyString());
    }

    private Message<ThoughtEvent> createThoughtMessage(UUID thoughtId, String content, String eventType) {
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setThoughtContent(content);
        event.setAuthor("Test Author");
        event.setAuthorBio("Test Bio");
        event.setStatus("IN_REVIEW");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        return wrapWithCloudEventMetadata(event, eventType);
    }

    private Message<ThoughtEvent> createThoughtMessageWithNullPayload(String eventType) {
        return wrapWithCloudEventMetadata(null, eventType);
    }

    @SuppressWarnings("unchecked")
    private Message<ThoughtEvent> wrapWithCloudEventMetadata(ThoughtEvent event, String eventType) {
        IncomingCloudEventMetadata<ThoughtEvent> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn(eventType);
        when(ceMetadata.getSource()).thenReturn(URI.create("/thoughts-backend"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());
        if (event != null && event.getThoughtId() != null) {
            when(ceMetadata.getSubject()).thenReturn(Optional.of(event.getThoughtId().toString()));
        }
        return Message.of(event, Metadata.of(ceMetadata));
    }
}
