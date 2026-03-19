package com.redhat.demos.evaluation.integration;

import com.redhat.demos.evaluation.consumer.ThoughtEvaluationConsumer;
import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import com.redhat.demos.evaluation.model.VectorType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class EndToEndEvaluationFlowTest {

    @Inject
    ThoughtEvaluationConsumer consumer;

    @BeforeEach
    @Transactional
    public void setup() {
        ThoughtEvaluation.deleteAll();
        EvaluationVector.deleteAll();

        seedTestVectors();
    }

    @Test
    @Transactional
    public void testCompleteFlowFromKafkaEventToDatabasePersistence() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "I am grateful for this beautiful day and all the wonderful opportunities";

        ThoughtEvent event = createThoughtEvent(thoughtId, thoughtContent);

        consumer.consumeThoughtEvent(event);

        List<ThoughtEvaluation> evaluations = ThoughtEvaluation.find("thoughtId", thoughtId).list();
        assertFalse(evaluations.isEmpty(), "Evaluation should be persisted in database");

        ThoughtEvaluation evaluation = evaluations.get(0);
        assertEquals(thoughtId, evaluation.thoughtId);
        assertEquals(ThoughtStatus.APPROVED, evaluation.status);
        assertNotNull(evaluation.similarityScore);
        assertNotNull(evaluation.evaluatedAt);
        assertNotNull(evaluation.metadata);
        assertTrue(evaluation.metadata.contains("correlationId"));
    }

    @Test
    @Transactional
    public void testCompleteFlowWithNegativeContent() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "I hate everything and everyone is terrible and awful";

        ThoughtEvent event = createThoughtEvent(thoughtId, thoughtContent);

        consumer.consumeThoughtEvent(event);

        List<ThoughtEvaluation> evaluations = ThoughtEvaluation.find("thoughtId", thoughtId).list();
        assertFalse(evaluations.isEmpty(), "Evaluation should be persisted in database");

        ThoughtEvaluation evaluation = evaluations.get(0);
        assertEquals(thoughtId, evaluation.thoughtId);
        assertEquals(ThoughtStatus.REJECTED, evaluation.status);
        assertNotNull(evaluation.similarityScore);
        assertTrue(evaluation.similarityScore.doubleValue() > 0.85,
            "Negative content should have high similarity to negative vectors");
    }

    @Test
    @Transactional
    public void testMultipleEventsProcessedSequentially() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();

        ThoughtEvent event1 = createThoughtEvent(thoughtId1, "Positive thought 1");
        ThoughtEvent event2 = createThoughtEvent(thoughtId2, "I hate this");
        ThoughtEvent event3 = createThoughtEvent(thoughtId3, "Positive thought 2");

        consumer.consumeThoughtEvent(event1);
        consumer.consumeThoughtEvent(event2);
        consumer.consumeThoughtEvent(event3);

        assertEquals(3, ThoughtEvaluation.count(), "All three evaluations should be persisted");

        ThoughtEvaluation eval1 = ThoughtEvaluation.find("thoughtId", thoughtId1).firstResult();
        ThoughtEvaluation eval2 = ThoughtEvaluation.find("thoughtId", thoughtId2).firstResult();
        ThoughtEvaluation eval3 = ThoughtEvaluation.find("thoughtId", thoughtId3).firstResult();

        assertNotNull(eval1);
        assertNotNull(eval2);
        assertNotNull(eval3);

        assertEquals(ThoughtStatus.APPROVED, eval1.status);
        assertEquals(ThoughtStatus.REJECTED, eval2.status);
        assertEquals(ThoughtStatus.APPROVED, eval3.status);
    }

    private ThoughtEvent createThoughtEvent(UUID thoughtId, String content) {
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setThoughtContent(content);
        event.setAuthor("Test Author");
        event.setAuthorBio("Test Bio");
        event.setStatus("IN_REVIEW");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setEventType("CREATED");
        return event;
    }

    private void seedTestVectors() {
        EvaluationVector negativeVector = new EvaluationVector();
        negativeVector.vectorType = VectorType.NEGATIVE;
        negativeVector.label = "Test negative vector";
        negativeVector.embedding = createNegativeVectorArray();
        negativeVector.persist();

        EvaluationVector positiveVector = new EvaluationVector();
        positiveVector.vectorType = VectorType.POSITIVE;
        positiveVector.label = "Test positive vector";
        positiveVector.embedding = createPositiveVectorArray();
        positiveVector.persist();
    }

    private float[] createNegativeVectorArray() {
        float[] vector = new float[384];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = -0.09f + (float) (Math.random() * 0.02f);
        }
        return vector;
    }

    private float[] createPositiveVectorArray() {
        float[] vector = new float[384];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (Math.random() * 0.2f - 0.1f);
        }
        return vector;
    }
}
