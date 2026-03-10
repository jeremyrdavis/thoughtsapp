package com.redhat.demos.thoughts.admin;

import com.redhat.demos.thoughts.admin.model.Thought;
import com.redhat.demos.thoughts.admin.model.ThoughtEvaluation;
import com.redhat.demos.thoughts.admin.model.ThoughtStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class EntityTest {

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    public void testThoughtPersistenceAndRetrieval() {
        Thought thought = new Thought();
        thought.content = "This is a test thought for persistence verification";
        thought.author = "Test Author";
        thought.authorBio = "Test bio";
        thought.persist();
        entityManager.flush();

        assertNotNull(thought.id);
        assertNotNull(thought.createdAt);
        assertNotNull(thought.updatedAt);
        assertEquals(ThoughtStatus.IN_REVIEW, thought.status);

        Thought found = Thought.findById(thought.id);
        assertNotNull(found);
        assertEquals("This is a test thought for persistence verification", found.content);
        assertEquals("Test Author", found.author);
    }

    @Test
    @Transactional
    public void testThoughtEvaluationPersistenceAndAssociation() {
        Thought thought = new Thought();
        thought.content = "A thought to be evaluated by the AI system";
        thought.author = "Eval Author";
        thought.authorBio = "Eval bio";
        thought.persist();
        entityManager.flush();

        ThoughtEvaluation evaluation = new ThoughtEvaluation();
        evaluation.thoughtId = thought.id;
        evaluation.status = ThoughtStatus.APPROVED;
        evaluation.similarityScore = new BigDecimal("0.2500");
        evaluation.metadata = "{\"model\": \"test\"}";
        evaluation.persist();
        entityManager.flush();

        assertNotNull(evaluation.id);
        assertNotNull(evaluation.evaluatedAt);

        ThoughtEvaluation found = ThoughtEvaluation.findById(evaluation.id);
        assertNotNull(found);
        assertEquals(thought.id, found.thoughtId);
        assertEquals(ThoughtStatus.APPROVED, found.status);
    }

    @Test
    @Transactional
    public void testThoughtPagination() {
        // Use seed data from import.sql; there are 7 thoughts
        List<Thought> page0 = Thought.findAll().page(0, 3).list();
        assertTrue(page0.size() <= 3);

        List<Thought> page1 = Thought.findAll().page(1, 3).list();
        assertNotNull(page1);

        long totalCount = Thought.count();
        assertTrue(totalCount >= 3);
    }

    @Test
    @Transactional
    public void testThoughtCountByStatus() {
        // Seed data has APPROVED, IN_REVIEW, and REJECTED thoughts
        long approvedCount = Thought.count("status", ThoughtStatus.APPROVED);
        long rejectedCount = Thought.count("status", ThoughtStatus.REJECTED);
        long inReviewCount = Thought.count("status", ThoughtStatus.IN_REVIEW);

        assertTrue(approvedCount >= 1);
        assertTrue(rejectedCount >= 1);
        assertTrue(inReviewCount >= 1);
    }
}
