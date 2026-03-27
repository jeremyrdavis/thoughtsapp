package com.redhat.demos.evaluation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EvaluationServiceImpl implements EvaluationService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    EmbeddingService embeddingService;

    @Inject
    EntityManager em;

    @ConfigProperty(name = "evaluation.similarity.threshold", defaultValue = "0.85")
    double similarityThreshold;

    @ConfigProperty(name = "evaluation.model.name", defaultValue = "nomic-embed-text")
    String modelName;

    @Override
    @Transactional
    @Counted(value = "evaluation.thoughts.total", description = "Total number of thoughts evaluated")
    @Timed(value = "evaluation.thoughts.duration", description = "Time taken to evaluate thoughts")
    public ThoughtEvaluation evaluateThought(UUID thoughtId, String thoughtContent) {
        String correlationId = UUID.randomUUID().toString();

        Log.infof("[%s] Starting evaluation for thought %s", correlationId, thoughtId);

        try {
            float[] thoughtVector = embeddingService.generateEmbedding(thoughtContent);
            String vectorLiteral = arrayToVectorLiteral(thoughtVector);

            @SuppressWarnings("unchecked")
            List<Object[]> results = em.createNativeQuery(
                "SELECT id, label, (1 - (embedding <=> cast(:vec AS vector))) AS similarity " +
                "FROM evaluation_vectors WHERE vector_type = 'NEGATIVE' " +
                "ORDER BY embedding <=> cast(:vec AS vector) ASC LIMIT 1")
                .setParameter("vec", vectorLiteral)
                .getResultList();

            double maxSimilarity = 0.0;
            String matchedLabel = null;

            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                maxSimilarity = ((Number) row[2]).doubleValue();
                matchedLabel = (String) row[1];
            }

            Log.infof("[%s] Evaluation result: max similarity %.4f with '%s' (threshold: %.2f)",
                correlationId, maxSimilarity, matchedLabel, similarityThreshold);

            ThoughtStatus status = maxSimilarity > similarityThreshold
                ? ThoughtStatus.REJECTED
                : ThoughtStatus.APPROVED;

            ThoughtEvaluation evaluation = createEvaluation(thoughtId, status, maxSimilarity, matchedLabel, correlationId);

            Log.infof("[%s] Evaluation completed: %s (id: %s)", correlationId, status, evaluation.id);

            return evaluation;

        } catch (Exception e) {
            Log.errorf(e, "[%s] Failed to evaluate thought %s", correlationId, thoughtId);
            throw new RuntimeException("Evaluation failed: " + e.getMessage(), e);
        }
    }

    private ThoughtEvaluation createEvaluation(UUID thoughtId, ThoughtStatus status,
                                               double similarityScore, String matchedLabel,
                                               String correlationId) {
        ThoughtEvaluation evaluation = new ThoughtEvaluation();
        evaluation.thoughtId = thoughtId;
        evaluation.status = status;
        evaluation.similarityScore = BigDecimal.valueOf(similarityScore);

        ObjectNode metadata = MAPPER.createObjectNode();
        metadata.put("evaluationTimestamp", LocalDateTime.now().toString());
        metadata.put("correlationId", correlationId);
        metadata.put("threshold", similarityThreshold);
        metadata.put("modelName", modelName);

        if (matchedLabel != null) {
            metadata.put("matchedNegativeLabel", matchedLabel);
        }

        evaluation.metadata = metadata.toString();
        evaluation.persist();

        return evaluation;
    }

    private String arrayToVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(vector[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
