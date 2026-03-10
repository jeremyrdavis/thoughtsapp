package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.model.ThoughtEvaluation;
import com.redhat.demos.thoughts.admin.model.ThoughtStatus;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Path("/evaluations")
@Produces(MediaType.TEXT_HTML)
public class EvaluationResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance evaluations(
                List<ThoughtEvaluation> evaluations,
                int page,
                int size,
                boolean hasMore,
                long totalCount,
                String currentStatus
        );

        public static native TemplateInstance stats(
                long totalEvaluated,
                long approvedCount,
                long rejectedCount,
                BigDecimal averageSimilarityScore,
                BigDecimal approvedPercentage,
                BigDecimal rejectedPercentage
        );
    }

    @GET
    public TemplateInstance list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("status") String status) {

        List<ThoughtEvaluation> evaluationList;
        long totalCount;

        if (status != null && !status.isBlank()) {
            ThoughtStatus filterStatus = ThoughtStatus.valueOf(status);
            evaluationList = ThoughtEvaluation.find("status", filterStatus)
                    .page(page, size)
                    .list();
            totalCount = ThoughtEvaluation.count("status", filterStatus);
        } else {
            evaluationList = ThoughtEvaluation.findAll()
                    .page(page, size)
                    .list();
            totalCount = ThoughtEvaluation.count();
        }

        boolean hasMore = (long) (page + 1) * size < totalCount;

        return Templates.evaluations(evaluationList, page, size, hasMore, totalCount,
                status != null ? status : "");
    }

    @GET
    @Path("/stats")
    public TemplateInstance stats() {
        long totalEvaluated = ThoughtEvaluation.count();
        long approvedCount = ThoughtEvaluation.count("status", ThoughtStatus.APPROVED);
        long rejectedCount = ThoughtEvaluation.count("status", ThoughtStatus.REJECTED);

        BigDecimal averageSimilarityScore = calculateAverageSimilarityScore();

        BigDecimal approvedPercentage;
        BigDecimal rejectedPercentage;

        if (totalEvaluated > 0) {
            approvedPercentage = BigDecimal.valueOf(approvedCount)
                    .divide(BigDecimal.valueOf(totalEvaluated), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            rejectedPercentage = BigDecimal.valueOf(rejectedCount)
                    .divide(BigDecimal.valueOf(totalEvaluated), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            approvedPercentage = BigDecimal.ZERO;
            rejectedPercentage = BigDecimal.ZERO;
        }

        return Templates.stats(totalEvaluated, approvedCount, rejectedCount,
                averageSimilarityScore, approvedPercentage, rejectedPercentage);
    }

    private BigDecimal calculateAverageSimilarityScore() {
        List<ThoughtEvaluation> allEvaluations = ThoughtEvaluation.listAll();

        if (allEvaluations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = allEvaluations.stream()
                .map(evaluation -> evaluation.similarityScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                BigDecimal.valueOf(allEvaluations.size()),
                4,
                RoundingMode.HALF_UP
        );
    }
}
