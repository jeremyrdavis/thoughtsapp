package com.redhat.demos.evaluation.service;

import com.redhat.demos.evaluation.dto.VectorInitializationResultDTO;
import com.redhat.demos.evaluation.dto.VectorStatusDTO;
import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.VectorType;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class VectorInitializationService {

    private static final Map<String, VectorType> REFERENCE_PHRASES = new LinkedHashMap<>();

    static {
        REFERENCE_PHRASES.put("Who is the happier man, he who has braved the storm of life and lived, or he who has stayed securely on shore and merely existed?", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("Every single cell in the human body replaces itself over a period of seven years. That means there’s not even the smallest part of you now that was part of you seven years ago.", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("I hope that in this year to come, you make mistakes. Because if you are making mistakes, then you are making new things, trying new things, learning, living, pushing yourself, changing yourself, changing your world. You’re doing things you've never done before, and more importantly, you’re doing something.", VectorType.POSITIVE);
        REFERENCE_PHRASES.put("Death is the solution to all problems. No man - no problem.", VectorType.NEGATIVE);
        REFERENCE_PHRASES.put("There are corpses on Mount Everest that were once highly motivated people.", VectorType.NEGATIVE);
        REFERENCE_PHRASES.put("I can picture in my mind a world without war, a world without hate. And I can picture us attacking that world, because they'd never expect it.", VectorType.NEGATIVE);
    }

    @Inject
    EmbeddingService embeddingService;

    @ConfigProperty(name = "evaluation.vectors.auto-initialize", defaultValue = "true")
    boolean autoInitialize;

    void onStartup(@Observes StartupEvent event) {
        if (!autoInitialize) {
            Log.info("Vector auto-initialization disabled");
            return;
        }
        long existing = EvaluationVector.count();
        if (existing == 0) {
            Log.info("No evaluation vectors found — auto-initializing reference vectors");
            initializeVectors();
        } else {
            Log.infof("Found %d existing evaluation vectors, skipping auto-initialization", existing);
        }
    }

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
            vector.label = phrase.length() > 255 ? phrase.substring(0, 255) : phrase;
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
