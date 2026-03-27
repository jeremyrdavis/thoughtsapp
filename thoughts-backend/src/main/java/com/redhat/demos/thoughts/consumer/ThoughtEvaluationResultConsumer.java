package com.redhat.demos.thoughts.consumer;

import com.redhat.demos.thoughts.dto.ThoughtEvaluationResultDTO;
import com.redhat.demos.thoughts.model.Thought;
import com.redhat.demos.thoughts.model.ThoughtStatus;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ThoughtEvaluationResultConsumer {

    private static final String EVENT_TYPE_COMPLETED = "com.redhat.demos.evaluation.completed";

    @Incoming("evaluation-results")
    @Blocking
    public CompletionStage<Void> consumeEvaluationResult(Message message) {
        UUID correlationId = UUID.randomUUID();

        try {
            @SuppressWarnings("unchecked")
            Optional<IncomingCloudEventMetadata> ceMetadata =
                message.getMetadata(IncomingCloudEventMetadata.class);

            String eventType = ceMetadata.map(IncomingCloudEventMetadata::getType).orElse(null);

            Log.debugf("[%s] Received CloudEvent [type=%s]", correlationId, eventType);

            if (!EVENT_TYPE_COMPLETED.equals(eventType)) {
                Log.debugf("[%s] Ignoring non-evaluation event type: %s", correlationId, eventType);
                return message.ack();
            }

            JsonObject payload = (JsonObject) message.getPayload();
            if (payload == null) {
                Log.warnf("[%s] Received null payload, skipping", correlationId);
                return message.ack();
            }

            ThoughtEvaluationResultDTO result = ThoughtEvaluationResultDTO.fromJson(payload);

            if (result.thoughtId() == null || result.status() == null) {
                Log.warnf("[%s] Evaluation result missing required fields (thoughtId or status), skipping", correlationId);
                return message.ack();
            }

            updateThoughtStatus(result, correlationId);

        } catch (Exception e) {
            Log.errorf(e, "[%s] Error processing evaluation result: %s", correlationId, e.getMessage());
        }

        return message.ack();
    }

    @Transactional
    void updateThoughtStatus(ThoughtEvaluationResultDTO result, UUID correlationId) {
        Thought thought = Thought.findById(result.thoughtId());
        if (thought == null) {
            Log.warnf("[%s] Thought not found for id: %s, skipping status update", correlationId, result.thoughtId());
            return;
        }

        ThoughtStatus newStatus = ThoughtStatus.valueOf(result.status());
        Log.infof("[%s] Updating thought %s status from %s to %s",
            correlationId, result.thoughtId(), thought.status, newStatus);
        thought.status = newStatus;
        thought.persist();
    }
}
