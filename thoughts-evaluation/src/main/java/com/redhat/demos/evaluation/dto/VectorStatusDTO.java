package com.redhat.demos.evaluation.dto;

public record VectorStatusDTO(
        long totalVectors,
        long positiveCount,
        long negativeCount,
        boolean initialized
) {
}
