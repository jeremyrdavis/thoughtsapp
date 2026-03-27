package com.redhat.demos.thoughts.service;

import com.redhat.demos.thoughts.model.Thought;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ThoughtEventServiceTest {

    @Inject
    @Any
    InMemoryConnector connector;

    @BeforeEach
    @Transactional
    public void setup() {
        Thought.deleteAll();
        connector.sink("thoughts-events").clear();
    }

    @Test
    public void testEventPublishedOnCreate() {
        InMemorySink<Thought> thoughtsSink = connector.sink("thoughts-events");

        given()
                .contentType(ContentType.JSON)
                .body("{\"content\": \"Event publishing test for create operation\"}")
                .when()
                .post("/thoughts")
                .then()
                .statusCode(201);

        assertEquals(1, thoughtsSink.received().size());
        Message<Thought> message = thoughtsSink.received().get(0);
        Thought published = message.getPayload();
        assertEquals("Event publishing test for create operation", published.content);

        Optional<OutgoingCloudEventMetadata> ceMetadata = message.getMetadata(OutgoingCloudEventMetadata.class);
        assertTrue(ceMetadata.isPresent(), "CloudEvents metadata should be present");
        assertEquals("com.redhat.demos.thoughts.created", ceMetadata.get().getType());
        assertEquals(URI.create("/thoughts-backend"), ceMetadata.get().getSource());
        assertEquals(Optional.of(published.id.toString()), ceMetadata.get().getSubject());
        assertNotNull(ceMetadata.get().getId());
    }

    @Test
    public void testEventPublishedOnUpdate() {
        Thought thought = createTestThought("Original content for update event test");
        InMemorySink<Thought> thoughtsSink = connector.sink("thoughts-events");
        thoughtsSink.clear();

        given()
                .contentType(ContentType.JSON)
                .body("{\"content\": \"Updated content for event publishing\"}")
                .when()
                .put("/thoughts/" + thought.id)
                .then()
                .statusCode(200);

        assertEquals(1, thoughtsSink.received().size());
        Message<Thought> message = thoughtsSink.received().get(0);
        Thought published = message.getPayload();
        assertEquals("Updated content for event publishing", published.content);

        Optional<OutgoingCloudEventMetadata> ceMetadata = message.getMetadata(OutgoingCloudEventMetadata.class);
        assertTrue(ceMetadata.isPresent(), "CloudEvents metadata should be present");
        assertEquals("com.redhat.demos.thoughts.updated", ceMetadata.get().getType());
        assertEquals(URI.create("/thoughts-backend"), ceMetadata.get().getSource());
    }

    @Test
    public void testEventPublishedOnDelete() {
        Thought thought = createTestThought("Content for delete event test");
        InMemorySink<Thought> thoughtsSink = connector.sink("thoughts-events");
        thoughtsSink.clear();

        given()
                .when()
                .delete("/thoughts/" + thought.id)
                .then()
                .statusCode(204);

        assertEquals(1, thoughtsSink.received().size());
        Message<Thought> deleteMessage = thoughtsSink.received().get(0);
        Optional<OutgoingCloudEventMetadata> ceMetadata = deleteMessage.getMetadata(OutgoingCloudEventMetadata.class);
        assertTrue(ceMetadata.isPresent(), "CloudEvents metadata should be present");
        assertEquals("com.redhat.demos.thoughts.deleted", ceMetadata.get().getType());
    }

    @Test
    public void testEventIncludesAuthorFieldsOnCreate() {
        InMemorySink<Thought> thoughtsSink = connector.sink("thoughts-events");

        given()
                .contentType(ContentType.JSON)
                .body("{\"content\": \"Testing author fields in Kafka event payload\", \"author\": \"Marcus Aurelius\", \"authorBio\": \"Roman Emperor and Stoic philosopher\"}")
                .when()
                .post("/thoughts")
                .then()
                .statusCode(201);

        assertEquals(1, thoughtsSink.received().size());
        Thought published = thoughtsSink.received().get(0).getPayload();
        assertEquals("Testing author fields in Kafka event payload", published.content);
        assertEquals("Marcus Aurelius", published.author);
        assertEquals("Roman Emperor and Stoic philosopher", published.authorBio);
    }

    @Test
    public void testEventIncludesDefaultAuthorWhenNotProvided() {
        InMemorySink<Thought> thoughtsSink = connector.sink("thoughts-events");

        given()
                .contentType(ContentType.JSON)
                .body("{\"content\": \"Testing default author in Kafka event payload\"}")
                .when()
                .post("/thoughts")
                .then()
                .statusCode(201);

        assertEquals(1, thoughtsSink.received().size());
        Thought published = thoughtsSink.received().get(0).getPayload();
        assertEquals("Testing default author in Kafka event payload", published.content);
        assertEquals("Unknown", published.author);
        assertEquals("Unknown", published.authorBio);
    }

    @Transactional
    protected Thought createTestThought(String content) {
        Thought thought = new Thought();
        thought.content = content;
        thought.persist();
        return thought;
    }
}
