package com.redhat.demos.evaluation.service;

import com.redhat.demos.evaluation.dto.EvaluationResultEvent;
import com.redhat.demos.evaluation.model.ThoughtEvaluation;
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
public class EvaluationEventService {

    private static final Logger LOG = Logger.getLogger(EvaluationEventService.class);
    private static final String SOURCE = "/ai-evaluation-service";
    private static final String TYPE = "com.redhat.demos.evaluation.completed";

    @Channel("evaluation-results")
    MutinyEmitter<EvaluationResultEvent> evaluationEmitter;

    public void publishEvaluationCompleted(ThoughtEvaluation evaluation) {
        EvaluationResultEvent event = new EvaluationResultEvent(
            evaluation.thoughtId,
            evaluation.status.name(),
            evaluation.similarityScore,
            evaluation.evaluatedAt,
            evaluation.metadata
        );

        OutgoingCloudEventMetadata<?> ceMetadata = OutgoingCloudEventMetadata.builder()
                .withId(UUID.randomUUID().toString())
                .withType(TYPE)
                .withSource(URI.create(SOURCE))
                .withSubject(evaluation.thoughtId.toString())
                .withTimestamp(ZonedDateTime.now())
                .build();

        Message<EvaluationResultEvent> message = Message.of(event).addMetadata(ceMetadata);
        evaluationEmitter.sendMessageAndAwait(message);
        LOG.infof("Published CloudEvent [type=%s] for thought: %s with status: %s",
            TYPE, evaluation.thoughtId, evaluation.status);
    }
}
