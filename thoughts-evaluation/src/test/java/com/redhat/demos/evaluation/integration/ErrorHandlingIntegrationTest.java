package com.redhat.demos.evaluation.integration;

import com.redhat.demos.evaluation.consumer.ThoughtEvaluationConsumer;
import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.model.EvaluationVector;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.model.ThoughtStatus;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ErrorHandlingIntegrationTest {

    @Inject
    ThoughtEvaluationConsumer consumer;

    @InjectMock
    EvaluationService evaluationService;

    @BeforeEach
    @Transactional
    public void setup() {
        ThoughtEvaluation.deleteAll();
        EvaluationVector.deleteAll();
    }

    @Test
    public void testConsumerContinuesProcessingAfterError() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();

        ThoughtEvent event1 = createThoughtEvent(thoughtId1, "First thought");
        ThoughtEvent event2 = createThoughtEvent(thoughtId2, "Second thought");
        ThoughtEvent event3 = createThoughtEvent(thoughtId3, "Third thought");

        ThoughtEvaluation mockEval = createMockEvaluation(thoughtId1);

        when(evaluationService.evaluateThought(eq(thoughtId1), any())).thenReturn(mockEval);
        when(evaluationService.evaluateThought(eq(thoughtId2), any()))
            .thenThrow(new RuntimeException("Evaluation failed"));
        when(evaluationService.evaluateThought(eq(thoughtId3), any())).thenReturn(mockEval);

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event1));
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event2));
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event3));

        verify(evaluationService, times(1)).evaluateThought(thoughtId1, "First thought");
        verify(evaluationService, times(1)).evaluateThought(thoughtId2, "Second thought");
        verify(evaluationService, times(1)).evaluateThought(thoughtId3, "Third thought");
    }

    @Test
    public void testNullEventHandledGracefully() {
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(null));
        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testNullAndEmptyContentHandledGracefully() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();

        ThoughtEvent event1 = createThoughtEvent(thoughtId1, null);
        ThoughtEvent event2 = createThoughtEvent(thoughtId2, "");
        ThoughtEvent event3 = createThoughtEvent(thoughtId3, "   ");

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event1));
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event2));
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(event3));

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testMissingRequiredFieldsHandledGracefully() {
        ThoughtEvent eventNoId = new ThoughtEvent();
        eventNoId.setThoughtContent("Content without ID");
        eventNoId.setEventType("CREATED");

        ThoughtEvent eventNoContent = new ThoughtEvent();
        eventNoContent.setThoughtId(UUID.randomUUID());
        eventNoContent.setEventType("CREATED");

        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(eventNoId));
        assertDoesNotThrow(() -> consumer.consumeThoughtEvent(eventNoContent));

        verify(evaluationService, never()).evaluateThought(any(), any());
    }

    @Test
    public void testConsumerRecoveryAfterMultipleFailures() {
        UUID thoughtId1 = UUID.randomUUID();
        UUID thoughtId2 = UUID.randomUUID();
        UUID thoughtId3 = UUID.randomUUID();
        UUID thoughtId4 = UUID.randomUUID();

        ThoughtEvent event1 = createThoughtEvent(thoughtId1, "Thought 1");
        ThoughtEvent event2 = createThoughtEvent(thoughtId2, "Thought 2");
        ThoughtEvent event3 = createThoughtEvent(thoughtId3, "Thought 3");
        ThoughtEvent event4 = createThoughtEvent(thoughtId4, "Thought 4");

        ThoughtEvaluation mockEval = createMockEvaluation(thoughtId1);

        when(evaluationService.evaluateThought(eq(thoughtId1), any()))
            .thenThrow(new RuntimeException("Fail 1"));
        when(evaluationService.evaluateThought(eq(thoughtId2), any()))
            .thenThrow(new RuntimeException("Fail 2"));
        when(evaluationService.evaluateThought(eq(thoughtId3), any())).thenReturn(mockEval);
        when(evaluationService.evaluateThought(eq(thoughtId4), any())).thenReturn(mockEval);

        consumer.consumeThoughtEvent(event1);
        consumer.consumeThoughtEvent(event2);
        consumer.consumeThoughtEvent(event3);
        consumer.consumeThoughtEvent(event4);

        verify(evaluationService, times(4)).evaluateThought(any(UUID.class), anyString());
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

    private ThoughtEvaluation createMockEvaluation(UUID thoughtId) {
        ThoughtEvaluation evaluation = new ThoughtEvaluation();
        evaluation.id = UUID.randomUUID();
        evaluation.thoughtId = thoughtId;
        evaluation.status = ThoughtStatus.APPROVED;
        evaluation.similarityScore = new BigDecimal("0.75");
        evaluation.metadata = "{}";
        return evaluation;
    }
}
