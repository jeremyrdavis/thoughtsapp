package com.redhat.demos.evaluation.service;

import com.redhat.demos.evaluation.dto.EvaluationResultEvent;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class EvaluationEventServiceTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    EvaluationEventService evaluationEventService;

    @BeforeEach
    public void setup() {
        connector.sink("evaluation-results").clear();
    }

    @Test
    public void testPublishEvaluationCompleted() {
        InMemorySink<EvaluationResultEvent> sink = connector.sink("evaluation-results");

        ThoughtEvaluation evaluation = createTestEvaluation(ThoughtStatus.APPROVED, new BigDecimal("0.42"));

        evaluationEventService.publishEvaluationCompleted(evaluation);

        assertEquals(1, sink.received().size());
        Message<EvaluationResultEvent> message = sink.received().get(0);
        EvaluationResultEvent event = message.getPayload();

        assertEquals(evaluation.thoughtId, event.thoughtId());
        assertEquals("APPROVED", event.status());
        assertEquals(evaluation.similarityScore, event.similarityScore());
        assertEquals(evaluation.evaluatedAt, event.evaluatedAt());
        assertEquals(evaluation.metadata, event.metadata());
    }

    @Test
    public void testPublishEvaluationCompletedWithCloudEventsMetadata() {
        InMemorySink<EvaluationResultEvent> sink = connector.sink("evaluation-results");

        ThoughtEvaluation evaluation = createTestEvaluation(ThoughtStatus.REJECTED, new BigDecimal("0.92"));

        evaluationEventService.publishEvaluationCompleted(evaluation);

        assertEquals(1, sink.received().size());
        Message<EvaluationResultEvent> message = sink.received().get(0);

        @SuppressWarnings("unchecked")
        Optional<OutgoingCloudEventMetadata> ceMetadata = message.getMetadata(OutgoingCloudEventMetadata.class);
        assertTrue(ceMetadata.isPresent(), "CloudEvents metadata should be present");
        assertEquals("com.redhat.demos.evaluation.completed", ceMetadata.get().getType());
        assertEquals(URI.create("/ai-evaluation-service"), ceMetadata.get().getSource());
        assertEquals(Optional.of(evaluation.thoughtId.toString()), ceMetadata.get().getSubject());
        assertNotNull(ceMetadata.get().getId());
    }

    @Test
    public void testPublishMultipleEvaluations() {
        InMemorySink<EvaluationResultEvent> sink = connector.sink("evaluation-results");

        ThoughtEvaluation eval1 = createTestEvaluation(ThoughtStatus.APPROVED, new BigDecimal("0.30"));
        ThoughtEvaluation eval2 = createTestEvaluation(ThoughtStatus.REJECTED, new BigDecimal("0.95"));

        evaluationEventService.publishEvaluationCompleted(eval1);
        evaluationEventService.publishEvaluationCompleted(eval2);

        assertEquals(2, sink.received().size());

        EvaluationResultEvent event1 = sink.received().get(0).getPayload();
        EvaluationResultEvent event2 = sink.received().get(1).getPayload();

        assertEquals("APPROVED", event1.status());
        assertEquals("REJECTED", event2.status());
    }

    private ThoughtEvaluation createTestEvaluation(ThoughtStatus status, BigDecimal score) {
        ThoughtEvaluation evaluation = new ThoughtEvaluation();
        evaluation.id = UUID.randomUUID();
        evaluation.thoughtId = UUID.randomUUID();
        evaluation.status = status;
        evaluation.similarityScore = score;
        evaluation.evaluatedAt = LocalDateTime.now();
        evaluation.metadata = "{\"correlationId\":\"test\"}";
        return evaluation;
    }
}
