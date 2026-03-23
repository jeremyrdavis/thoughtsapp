package com.redhat.demos.evaluation.service;

import com.redhat.demos.evaluation.dto.VectorInitializationResultDTO;
import com.redhat.demos.evaluation.dto.VectorStatusDTO;
import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.VectorType;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class VectorInitializationService {

    private static final Map<String, VectorType> REFERENCE_PHRASES = new LinkedHashMap<>();

    static {
        REFERENCE_PHRASES.put("Encouraging and uplifting language", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("Optimistic and hopeful perspective", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("Gratitude and appreciation expressions", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("Hateful or discriminatory language", VectorType.NEGATIVE);
        REFERENCE_PHRASES.put("Violent or threatening content", VectorType.NEGATIVE);
        REFERENCE_PHRASES.put("Profanity and abusive language", VectorType.NEGATIVE);
    }

    @Inject
    EmbeddingService embeddingService;

    @Transactional
    public VectorInitializationResultDTO initializeVectors() {
        Log.info("Starting vector initialization - deleting existing vectors");
        long deleted = EvaluationVector.deleteAll();
        Log.infof("Deleted %d existing vectors", deleted);

        long positiveCount = 0;
        long negativeCount = 0;

        for (Map.Entry<String, VectorType> entry : REFERENCE_PHRASES.entrySet()) {
            String phrase = entry.getKey();
            VectorType type = entry.getValue();

            Log.infof("Generating embedding for: %s (%s)", phrase, type);
            float[] embedding = embeddingService.generateEmbedding(phrase);

            EvaluationVector vector = new EvaluationVector();
            vector.embedding = embedding;
            vector.vectorType = type;
            vector.label = phrase;
            vector.persist();

            if (type == VectorType.POSITIVE) {
                positiveCount++;
            } else {
                negativeCount++;
            }
        }

        long totalCreated = positiveCount + negativeCount;
        String message = String.format("Successfully initialized %d vectors (%d positive, %d negative)",
                totalCreated, positiveCount, negativeCount);
        Log.info(message);

        return new VectorInitializationResultDTO(totalCreated, positiveCount, negativeCount, message);
    }

    public VectorStatusDTO getVectorStatus() {
        long totalVectors = EvaluationVector.count();
        long positiveCount = EvaluationVector.count("vectorType", VectorType.POSITIVE);
        long negativeCount = EvaluationVector.count("vectorType", VectorType.NEGATIVE);

        return new VectorStatusDTO(totalVectors, positiveCount, negativeCount, totalVectors > 0);
    }
}
