package com.redhat.demos.evaluation.dto;

public record VectorInitializationResultDTO(
        long vectorsCreated,
        long positiveCount,
        long negativeCount,
        String message
) {
}
