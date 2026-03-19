package com.redhat.demos.evaluation.consumer;

import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ThoughtEvaluationConsumerTest {

    @Inject
    ThoughtEvaluationConsumer consumer;

    @InjectMock
    EvaluationService evaluationService;

    @Test
    void testConsumeValidThoughtCreatedEvent() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "This is a positive thought about the future";

        ThoughtEvent event = createThoughtEvent(thoughtId, thoughtContent);

        consumer.consumeThoughtEvent(event);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(evaluationService, times(1)).evaluateThought(idCaptor.capture(), contentCaptor.capture());
        assertEquals(thoughtId, idCaptor.getValue());
        assertEquals(thoughtContent, contentCaptor.getValue());
    }

    @Test
    void testConsumeNullEvent() {
        consumer.consumeThoughtEvent(null);

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingThoughtId() {
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtContent("Some content");
        event.setEventType("CREATED");

        consumer.consumeThoughtEvent(event);

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithMissingContent() {
        UUID thoughtId = UUID.randomUUID();
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setEventType("CREATED");

        consumer.consumeThoughtEvent(event);

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEmptyContent() {
        UUID thoughtId = UUID.randomUUID();
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setThoughtContent("   ");
        event.setEventType("CREATED");

        consumer.consumeThoughtEvent(event);

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    void testConsumeEventWithEvaluationServiceException() {
        UUID thoughtId = UUID.randomUUID();
        String thoughtContent = "Test content";

        ThoughtEvent event = createThoughtEvent(thoughtId, thoughtContent);

        doThrow(new RuntimeException("Evaluation failed")).when(evaluationService)
            .evaluateThought(any(), any());

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event));

        verify(evaluationService, times(1)).evaluateThought(thoughtId, thoughtContent);
    }

    @Test
    void testExtractThoughtIdAndContentCorrectly() {
        UUID expectedId = UUID.randomUUID();
        String expectedContent = "Specific thought content for extraction test";

        ThoughtEvent event = createThoughtEvent(expectedId, expectedContent);

        consumer.consumeThoughtEvent(event);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        verify(evaluationService, times(1)).evaluateThought(idCaptor.capture(), contentCaptor.capture());
        assertEquals(expectedId, idCaptor.getValue());
        assertEquals(expectedContent, contentCaptor.getValue());
    }

    @Test
    void testConsumeMultipleEventsSequentially() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();

        ThoughtEvent event1 = createThoughtEvent(thoughtId1, "First thought");
        ThoughtEvent event2 = createThoughtEvent(thoughtId2, "Second thought");

        consumer.consumeThoughtEvent(event1);
        consumer.consumeThoughtEvent(event2);

        verify(evaluationService, times(2)).evaluateThought(any(UUID.class), anyString());
    }

    private ThoughtEvent createThoughtEvent(UUID thoughtId, String content) {
        ThoughtEvent event = new ThoughtEvent();
        event.setThoughtId(thoughtId);
        event.setThoughtContent(content);
        event.setAuthor("Test Author");
        event.setAuthorBio("Test Bio");
        event.setStatus("IN_REVIEW");
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setEventType("CREATED");
        return event;
    }
}
