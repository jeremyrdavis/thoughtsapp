package com.redhat.demos.thoughts.service;

import com.redhat.demos.thoughts.model.Thought;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
public class ThoughtEventService {

    private static final Logger LOG = Logger.getLogger(ThoughtEventService.class);
    private static final String SOURCE = "/thoughts-backend";
    private static final String TYPE_PREFIX = "com.redhat.demos.thoughts.";

    @Channel("thoughts-events")
    MutinyEmitter<Thought> thoughtsEmitter;

    public void publishThoughtCreated(Thought thought) {
        publishEvent(thought, "created");
    }

    public void publishThoughtUpdated(Thought thought) {
        publishEvent(thought, "updated");
    }

    public void publishThoughtDeleted(Thought thought) {
        publishEvent(thought, "deleted");
    }

    private void publishEvent(Thought thought, String eventType) {
        OutgoingCloudEventMetadata<?> ceMetadata = OutgoingCloudEventMetadata.builder()
                .withId(UUID.randomUUID().toString())
                .withType(TYPE_PREFIX + eventType)
                .withSource(URI.create(SOURCE))
                .withSubject(thought.id.toString())
                .withTimestamp(ZonedDateTime.now())
                .build();

        Message<Thought> message = Message.of(thought).addMetadata(ceMetadata);
        thoughtsEmitter.sendMessageAndAwait(message);
        LOG.infof("Published CloudEvent [type=%s%s] for thought: %s", TYPE_PREFIX, eventType, thought.id);
    }
}
