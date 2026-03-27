package com.redhat.demos.evaluation.integration;

import com.redhat.demos.evaluation.consumer.ThoughtEvaluationConsumer;
import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ErrorHandlingIntegrationTest {

    @Inject
    ThoughtEvaluationConsumer consumer;

    @InjectMock
    EvaluationService evaluationService;

    @BeforeEach
    @Transactional
    public void setup() {
        ThoughtEvaluation.deleteAll();
        EvaluationVector.deleteAll();
    }

    @Test
    public void testConsumerContinuesProcessingAfterError() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();

        Message<JsonObject> message1 = createThoughtMessage(thoughtId1, "First thought");
        Message<JsonObject> message2 = createThoughtMessage(thoughtId2, "Second thought");
        Message<JsonObject> message3 = createThoughtMessage(thoughtId3, "Third thought");

        ThoughtEvaluation mockEval = createMockEvaluation(thoughtId1);

        when(evaluationService.evaluateThought(eq(thoughtId1), any())).thenReturn(mockEval);
        when(evaluationService.evaluateThought(eq(thoughtId2), any()))
            .thenThrow(new RuntimeException("Evaluation failed"));
        when(evaluationService.evaluateThought(eq(thoughtId3), any())).thenReturn(mockEval);

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message1).toCompletableFuture().join());
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message2).toCompletableFuture().join());
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message3).toCompletableFuture().join());

        verify(evaluationService, times(1)).evaluateThought(thoughtId1, "First thought");
        verify(evaluationService, times(1)).evaluateThought(thoughtId2, "Second thought");
        verify(evaluationService, times(1)).evaluateThought(thoughtId3, "Third thought");
    }

    @Test
    public void testNullPayloadHandledGracefully() {
        Message<JsonObject> message = createThoughtMessageWithNullPayload();
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message).toCompletableFuture().join());
        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testNullAndEmptyContentHandledGracefully() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();

        Message<JsonObject> message1 = createThoughtMessage(thoughtId1, null);
        Message<JsonObject> message2 = createThoughtMessage(thoughtId2, "");
        Message<JsonObject> message3 = createThoughtMessage(thoughtId3, "   ");

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message1).toCompletableFuture().join());
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message2).toCompletableFuture().join());
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(message3).toCompletableFuture().join());

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testMissingRequiredFieldsHandledGracefully() {
        JsonObject eventNoId = new JsonObject().put("content", "Content without ID");
        Message<JsonObject> messageNoId = wrapWithCloudEventMetadata(eventNoId);

        JsonObject eventNoContent = new JsonObject().put("id", UUID.randomUUID().toString());
        Message<JsonObject> messageNoContent = wrapWithCloudEventMetadata(eventNoContent);

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(messageNoId).toCompletableFuture().join());
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(messageNoContent).toCompletableFuture().join());

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testConsumerRecoveryAfterMultipleFailures() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();
        UUID thoughtId4 = UUID.randomUUID();

        Message<JsonObject> message1 = createThoughtMessage(thoughtId1, "Thought 1");
        Message<JsonObject> message2 = createThoughtMessage(thoughtId2, "Thought 2");
        Message<JsonObject> message3 = createThoughtMessage(thoughtId3, "Thought 3");
        Message<JsonObject> message4 = createThoughtMessage(thoughtId4, "Thought 4");

        ThoughtEvaluation mockEval = createMockEvaluation(thoughtId1);

        when(evaluationService.evaluateThought(eq(thoughtId1), any()))
            .thenThrow(new RuntimeException("Fail 1"));
        when(evaluationService.evaluateThought(eq(thoughtId2), any()))
            .thenThrow(new RuntimeException("Fail 2"));
        when(evaluationService.evaluateThought(eq(thoughtId3), any())).thenReturn(mockEval);
        when(evaluationService.evaluateThought(eq(thoughtId4), any())).thenReturn(mockEval);

        consumer.consumeThoughtEvent(message1).toCompletableFuture().join();
        consumer.consumeThoughtEvent(message2).toCompletableFuture().join();
        consumer.consumeThoughtEvent(message3).toCompletableFuture().join();
        consumer.consumeThoughtEvent(message4).toCompletableFuture().join();

        verify(evaluationService, times(4)).evaluateThought(any(UUID.class), anyString());
    }

    @SuppressWarnings("unchecked")
    private Message<JsonObject> createThoughtMessage(UUID thoughtId, String content) {
        JsonObject event = new JsonObject()
            .put("id", thoughtId != null ? thoughtId.toString() : null)
            .put("content", content)
            .put("author", "Test Author")
            .put("authorBio", "Test Bio")
            .put("status", "IN_REVIEW")
            .put("thumbsUp", 0)
            .put("thumbsDown", 0)
            .put("createdAt", LocalDateTime.now().toString())
            .put("updatedAt", LocalDateTime.now().toString());
        return wrapWithCloudEventMetadata(event);
    }

    @SuppressWarnings("unchecked")
    private Message<JsonObject> createThoughtMessageWithNullPayload() {
        IncomingCloudEventMetadata<JsonObject> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn("com.redhat.demos.thoughts.created");
        when(ceMetadata.getSource()).thenReturn(URI.create("/thoughts-backend"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());
        return Message.of(null, Metadata.of(ceMetadata));
    }

    @SuppressWarnings("unchecked")
    private Message<JsonObject> wrapWithCloudEventMetadata(JsonObject event) {
        IncomingCloudEventMetadata<JsonObject> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn("com.redhat.demos.thoughts.created");
        when(ceMetadata.getSource()).thenReturn(URI.create("/thoughts-backend"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());
        if (event != null && event.getString("id") != null) {
            when(ceMetadata.getSubject()).thenReturn(Optional.of(event.getString("id")));
        }
        return Message.of(event, Metadata.of(ceMetadata));
    }

    private ThoughtEvaluation createMockEvaluation(UUID thoughtId) {
        ThoughtEvaluation evaluation = new ThoughtEvaluation();
        evaluation.id = UUID.randomUUID();
        evaluation.thoughtId = thoughtId;
        evaluation.status = ThoughtStatus.APPROVED;
        evaluation.similarityScore = new BigDecimal("0.75");
        evaluation.metadata = "{}";
        return evaluation;
    }
}
