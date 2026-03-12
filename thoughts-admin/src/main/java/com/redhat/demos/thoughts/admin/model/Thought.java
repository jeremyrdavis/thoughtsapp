package com.redhat.demos.thoughts.admin.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class Thought {

    public UUID id;

    @NotBlank(message = "Thought content cannot be blank")
    @Size(min = 10, max = 500, message = "Thought content must be between 10 and 500 characters")
    public String content;

    public int thumbsUp;

    public int thumbsDown;

    public ThoughtStatus status;

    @Size(max = 200, message = "Author must be no more than 200 characters")
    public String author;

    @Size(max = 200, message = "Author bio must be no more than 200 characters")
    public String authorBio;

    public LocalDateTime createdAt;

    public LocalDateTime updatedAt;

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
