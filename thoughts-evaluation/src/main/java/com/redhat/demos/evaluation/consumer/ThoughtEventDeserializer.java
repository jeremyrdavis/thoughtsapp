package com.redhat.demos.evaluation.consumer;

import com.redhat.demos.evaluation.dto.ThoughtEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ThoughtEventDeserializer extends ObjectMapperDeserializer<ThoughtEvent> {

    public ThoughtEventDeserializer() {
        super(ThoughtEvent.class);
    }
}
