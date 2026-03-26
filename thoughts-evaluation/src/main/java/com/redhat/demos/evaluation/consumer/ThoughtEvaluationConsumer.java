package com.redhat.demos.evaluation.consumer;

import com.redhat.demos.evaluation.dto.ThoughtEvent;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
import com.redhat.demos.evaluation.service.EvaluationService;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer for thought events using CloudEvents structured format.
 * Listens to the thoughts-events topic and processes thought-created events only.
 * Event type filtering uses CloudEvents metadata rather than payload fields.
 */
@ApplicationScoped
public class ThoughtEvaluationConsumer {

    private static final String EVENT_TYPE_CREATED = "com.redhat.demos.thoughts.created";

    @Inject
    EvaluationService evaluationService;

    @Incoming("thoughts-events")
    @Blocking
    public CompletionStage<Void> consumeThoughtEvent(Message message) {
        UUID correlationId = UUID.randomUUID();

        Log.debugf("[%s] Received message with payload: %s and metadata: %s",
            correlationId, message.getPayload(), message.getMetadata());

        try {
            ThoughtEvent thoughtEvent = ThoughtEvent.fromJson((JsonObject) message.getPayload());
            Log.debugf("[%s] Deserialized ThoughtEvent: %s", correlationId, thoughtEvent);

            // Extract CloudEvents metadata
            Optional<IncomingCloudEventMetadata> ceMetadata =
                message.getMetadata(IncomingCloudEventMetadata.class);

            String eventType = ceMetadata.map(IncomingCloudEventMetadata::getType).orElse(null);
            String source = ceMetadata.map(m -> m.getSource().toString()).orElse(null);
            @SuppressWarnings("unchecked")
            Optional<String> subjectOpt = ceMetadata.flatMap(m -> m.getSubject());
            String subject = subjectOpt.orElse(null);

            Log.debugf("[%s] Received CloudEvent [type=%s, source=%s, subject=%s]",
                correlationId, eventType, source, subject);

            if (thoughtEvent == null) {
                Log.warnf("[%s] Failed to deserialize event data, skipping", correlationId);
                return message.ack();
            }

//            ThoughtEvent thoughtEvent = ThoughtEvent.fromJson(event);

            // Filter for thought-created events only using CloudEvents type
            if (!EVENT_TYPE_CREATED.equals(eventType)) {
                Log.debugf("[%s] Ignoring non-created event type: %s for thought: %s",
                    correlationId, eventType, thoughtEvent.id());
                return message.ack();
            }

            UUID thoughtId = thoughtEvent.id();
            String thoughtContent = thoughtEvent.content();

            if (thoughtId == null || thoughtContent == null || thoughtContent.trim().isEmpty()) {
                Log.warnf("[%s] Event missing required fields (thoughtId or content), skipping", correlationId);
                return message.ack();
            }

            Log.infof("[%s] Processing thought-created event for thought: %s", correlationId, thoughtEvent);

            ThoughtEvaluation thoughtEvaluation = evaluationService.evaluateThought(thoughtId, thoughtContent);
            Log.debugf("[%s] Evaluation result for thought %s: %s", correlationId, thoughtId, thoughtEvaluation);

            Log.infof("[%s] Successfully processed thought: %s", correlationId, thoughtId);

        } catch (Exception e) {
            Log.errorf(e, "[%s] Error processing thought event: %s", correlationId, e.getMessage());
        }

        return message.ack();
    }
}
