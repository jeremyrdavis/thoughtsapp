package com.redhat.demos.thoughts.consumer;

import com.redhat.demos.thoughts.model.Thought;
import com.redhat.demos.thoughts.model.ThoughtStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ThoughtEvaluationResultConsumerTest {

    @Inject
    ThoughtEvaluationResultConsumer consumer;

    @BeforeEach
    @Transactional
    public void setup() {
        Thought.deleteAll();
    }

    @Test
    @Transactional
    public void testEvaluationCompletedUpdatesThoughtStatus() {
        Thought thought = createTestThought("A positive thought about gratitude");

        assertEquals(ThoughtStatus.IN_REVIEW, thought.status);

        Message<JsonObject> message = createEvaluationResultMessage(
            thought.id, "APPROVED", "0.42", "com.redhat.demos.evaluation.completed");

        consumer.consumeEvaluationResult(message).toCompletableFuture().join();

        Thought updated = Thought.findById(thought.id);
        assertEquals(ThoughtStatus.APPROVED, updated.status);
    }

    @Test
    @Transactional
    public void testRejectedEvaluationUpdatesThoughtStatus() {
        Thought thought = createTestThought("A negative hateful thought");

        Message<JsonObject> message = createEvaluationResultMessage(
            thought.id, "REJECTED", "0.92", "com.redhat.demos.evaluation.completed");

        consumer.consumeEvaluationResult(message).toCompletableFuture().join();

        Thought updated = Thought.findById(thought.id);
        assertEquals(ThoughtStatus.REJECTED, updated.status);
    }

    @Test
    public void testNonEvaluationEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();

        Message<JsonObject> message = createEvaluationResultMessage(
            thoughtId, "APPROVED", "0.50", "com.redhat.demos.thoughts.created");

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Test
    public void testUpdatedEventTypeIsIgnored() {
        UUID thoughtId = UUID.randomUUID();

        Message<JsonObject> message = createEvaluationResultMessage(
            thoughtId, "APPROVED", "0.50", "com.redhat.demos.thoughts.updated");

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Test
    public void testNonexistentThoughtHandledGracefully() {
        UUID nonexistentId = UUID.randomUUID();

        Message<JsonObject> message = createEvaluationResultMessage(
            nonexistentId, "APPROVED", "0.50", "com.redhat.demos.evaluation.completed");

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Test
    public void testNullPayloadHandledGracefully() {
        @SuppressWarnings("unchecked")
        IncomingCloudEventMetadata<JsonObject> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn("com.redhat.demos.evaluation.completed");
        when(ceMetadata.getSource()).thenReturn(URI.create("/ai-evaluation-service"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());

        Message<JsonObject> message = Message.of(null, Metadata.of(ceMetadata));

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Test
    public void testMissingThoughtIdHandledGracefully() {
        JsonObject payload = new JsonObject()
            .put("status", "APPROVED")
            .put("similarityScore", "0.50");

        Message<JsonObject> message = wrapWithCloudEventMetadata(payload, "com.redhat.demos.evaluation.completed");

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Test
    public void testMissingStatusHandledGracefully() {
        JsonObject payload = new JsonObject()
            .put("thoughtId", UUID.randomUUID().toString())
            .put("similarityScore", "0.50");

        Message<JsonObject> message = wrapWithCloudEventMetadata(payload, "com.redhat.demos.evaluation.completed");

        assertDoesNotThrow(() -> consumer.consumeEvaluationResult(message).toCompletableFuture().join());
    }

    @Transactional
    Thought createTestThought(String content) {
        Thought thought = new Thought();
        thought.content = content;
        thought.author = "Test Author";
        thought.authorBio = "Test Bio";
        thought.status = ThoughtStatus.IN_REVIEW;
        thought.persist();
        return thought;
    }

    private Message<JsonObject> createEvaluationResultMessage(UUID thoughtId, String status, String score, String eventType) {
        JsonObject payload = new JsonObject()
            .put("thoughtId", thoughtId.toString())
            .put("status", status)
            .put("similarityScore", score)
            .put("evaluatedAt", LocalDateTime.now().toString())
            .put("metadata", "{\"correlationId\":\"test\"}");

        return wrapWithCloudEventMetadata(payload, eventType);
    }

    @SuppressWarnings("unchecked")
    private Message<JsonObject> wrapWithCloudEventMetadata(JsonObject payload, String eventType) {
        IncomingCloudEventMetadata<JsonObject> ceMetadata = mock(IncomingCloudEventMetadata.class);
        when(ceMetadata.getType()).thenReturn(eventType);
        when(ceMetadata.getSource()).thenReturn(URI.create("/ai-evaluation-service"));
        when(ceMetadata.getId()).thenReturn(UUID.randomUUID().toString());
        return Message.of(payload, Metadata.of(ceMetadata));
    }
}
