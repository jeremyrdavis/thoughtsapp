package com.redhat.demos.evaluation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EvaluationResultEvent(
    UUID thoughtId,
    String status,
    BigDecimal similarityScore,
    LocalDateTime evaluatedAt,
    String metadata
) {}
