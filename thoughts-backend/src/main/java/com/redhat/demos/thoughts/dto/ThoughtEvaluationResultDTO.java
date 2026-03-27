package com.redhat.demos.thoughts.dto;

import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ThoughtEvaluationResultDTO(
    UUID thoughtId,
    String status,
    BigDecimal similarityScore,
    LocalDateTime evaluatedAt,
    String metadata
) {
    public static ThoughtEvaluationResultDTO fromJson(JsonObject json) {
        return new ThoughtEvaluationResultDTO(
            json.getString("thoughtId") != null ? UUID.fromString(json.getString("thoughtId")) : null,
            json.getString("status"),
            json.getString("similarityScore") != null ? new BigDecimal(json.getString("similarityScore")) : null,
            json.getString("evaluatedAt") != null ? LocalDateTime.parse(json.getString("evaluatedAt")) : null,
            json.getString("metadata")
        );
    }
}
