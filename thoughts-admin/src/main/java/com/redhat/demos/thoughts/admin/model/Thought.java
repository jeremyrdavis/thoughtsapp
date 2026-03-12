package com.redhat.demos.thoughts.admin.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record Thought(
        UUID id,

        @NotBlank(message = "Thought content cannot be blank")
        @Size(min = 10, max = 500, message = "Thought content must be between 10 and 500 characters")
        String content,

        int thumbsUp,

        int thumbsDown,

        ThoughtStatus status,

        @Size(max = 200, message = "Author must be no more than 200 characters")
        String author,

        @Size(max = 200, message = "Author bio must be no more than 200 characters")
        String authorBio,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
    public String truncatedContent(int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
