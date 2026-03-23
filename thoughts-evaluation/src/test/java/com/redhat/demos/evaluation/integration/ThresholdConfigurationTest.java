package com.redhat.demos.evaluation.integration;

import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import com.redhat.demos.evaluation.model.VectorType;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ThresholdConfigurationTest {

    @Inject
    EvaluationService evaluationService;

    @ConfigProperty(name = "evaluation.similarity.threshold", defaultValue = "0.85")
    double configuredThreshold;

    @BeforeEach
    @Transactional
    public void setup() {
        ThoughtEvaluation.deleteAll();
        EvaluationVector.deleteAll();

        seedTestVectors();
    }

    @Test
    public void testDefaultThresholdIsLoaded() {
        assertEquals(0.85, configuredThreshold, 0.001,
            "Default threshold should be 0.85");
    }

    @Test
    @Transactional
    public void testThresholdAffectsEvaluationResult() {
        UUID thoughtId = UUID.randomUUID();
        String content = "This content has some negative aspects but also positive ones";

        ThoughtEvaluation evaluation = evaluationService.evaluateThought(thoughtId, content);

        assertNotNull(evaluation.metadata);
        assertTrue(evaluation.metadata.contains("threshold"),
            "Metadata should contain threshold configuration");
        assertTrue(evaluation.metadata.contains("0.85"),
            "Metadata should contain threshold value 0.85");
    }

    @Test
    @Transactional
    public void testEvaluationUsesConfiguredThreshold() {
        UUID thoughtId = UUID.randomUUID();
        String positiveContent = "I am grateful for all the wonderful opportunities and positive experiences";

        ThoughtEvaluation evaluation = evaluationService.evaluateThought(thoughtId, positiveContent);

        assertNotNull(evaluation);
        assertNotNull(evaluation.similarityScore);

        if (evaluation.similarityScore.doubleValue() <= configuredThreshold) {
            assertEquals(ThoughtStatus.APPROVED, evaluation.status,
                "Thoughts below threshold should be APPROVED");
        } else {
            assertEquals(ThoughtStatus.REJECTED, evaluation.status,
                "Thoughts above threshold should be REJECTED");
        }
    }

    @Test
    @Transactional
    public void testMetadataIncludesConfigurationDetails() {
        UUID thoughtId = UUID.randomUUID();
        String content = "Test content for metadata verification";

        ThoughtEvaluation evaluation = evaluationService.evaluateThought(thoughtId, content);

        assertNotNull(evaluation.metadata);
        assertTrue(evaluation.metadata.contains("correlationId"),
            "Metadata should include correlation ID");
        assertTrue(evaluation.metadata.contains("threshold"),
            "Metadata should include threshold value");
        assertTrue(evaluation.metadata.contains("evaluationTimestamp"),
            "Metadata should include timestamp");
        assertTrue(evaluation.metadata.contains("modelName"),
            "Metadata should include model name");
    }

    private void seedTestVectors() {
        EvaluationVector negativeVector = new EvaluationVector();
        negativeVector.vectorType = VectorType.NEGATIVE;
        negativeVector.label = "Test negative vector for threshold testing";
        negativeVector.embedding = createNegativeVectorArray();
        negativeVector.persist();

        EvaluationVector positiveVector = new EvaluationVector();
        positiveVector.vectorType = VectorType.POSITIVE;
        positiveVector.label = "Test positive vector for threshold testing";
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
