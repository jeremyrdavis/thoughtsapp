package com.redhat.demos.evaluation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for thought events consumed from Kafka.
 * Represents the event structure published by ThoughtEventService in thoughts-msa-ai-backend.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ThoughtEvent(
    UUID id,
    String content,
    String author,
    String authorBio,
    String status,
    Integer thumbsUp,
    Integer thumbsDown,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ThoughtEvent fromJson(JsonObject json) {
        return new ThoughtEvent(
            json.getString("id") != null ? UUID.fromString(json.getString("id")) : null,
            json.getString("content"),
            json.getString("author"),
            json.getString("authorBio"),
            json.getString("status"),
            json.getInteger("thumbsUp"),
            json.getInteger("thumbsDown"),
            json.getString("createdAt") != null ? LocalDateTime.parse(json.getString("createdAt")) : null,
            json.getString("updatedAt") != null ? LocalDateTime.parse(json.getString("updatedAt")) : null
        );
    }
}
