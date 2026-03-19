package com.redhat.demos.evaluation.service;

import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import com.redhat.demos.evaluation.model.VectorType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class EvaluationServiceTest {

    @Inject
    EvaluationService evaluationService;

    @BeforeEach
    @Transactional
    public void seedVectors() {
        // Clear existing vectors
        EvaluationVector.deleteAll();

        // Add a negative vector (high negative values similar to seeded data)
        EvaluationVector negativeVector = new EvaluationVector();
        negativeVector.vectorType = VectorType.NEGATIVE;
        negativeVector.label = "Test negative vector";
        negativeVector.embedding = createNegativeVectorArray();
        negativeVector.persist();

        // Add a positive vector
        EvaluationVector positiveVector = new EvaluationVector();
        positiveVector.vectorType = VectorType.POSITIVE;
        positiveVector.label = "Test positive vector";
        positiveVector.embedding = createPositiveVectorArray();
        positiveVector.persist();
    }

    @Test
    @Transactional
    public void testEvaluateThought_ApprovedWhenBelowThreshold() {
        UUID thoughtId = UUID.randomUUID();
        String positiveContent = "I am grateful for this wonderful day and all the opportunities ahead";

        ThoughtEvaluation result = evaluationService.evaluateThought(thoughtId, positiveContent);

        assertNotNull(result);
        assertEquals(thoughtId, result.thoughtId);
        assertEquals(ThoughtStatus.APPROVED, result.status);
        assertNotNull(result.similarityScore);
        assertTrue(result.similarityScore.doubleValue() <= 0.85);
    }

    @Test
    @Transactional
    public void testEvaluateThought_RejectedWhenAboveThreshold() {
        UUID thoughtId = UUID.randomUUID();
        String negativeContent = "I hate everyone and everything is terrible and awful";

        ThoughtEvaluation result = evaluationService.evaluateThought(thoughtId, negativeContent);

        assertNotNull(result);
        assertEquals(thoughtId, result.thoughtId);
        assertEquals(ThoughtStatus.REJECTED, result.status);
        assertNotNull(result.similarityScore);
        assertTrue(result.similarityScore.doubleValue() > 0.85);
    }

    @Test
    @Transactional
    public void testEvaluationPersistence() {
        UUID thoughtId = UUID.randomUUID();
        String content = "This is a test thought for persistence";

        ThoughtEvaluation result = evaluationService.evaluateThought(thoughtId, content);

        assertNotNull(result.id);
        assertNotNull(result.evaluatedAt);
        assertNotNull(result.metadata);
        assertTrue(result.metadata.contains("correlationId"));
        assertTrue(result.metadata.contains("threshold"));
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
