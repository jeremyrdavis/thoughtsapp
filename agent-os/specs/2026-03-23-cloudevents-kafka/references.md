# References for CloudEvents Kafka Migration

## Similar Implementations

### Current Kafka Producer (ThoughtEventService)

- **Location:** `thoughts-backend/src/main/java/com/redhat/demos/thoughts/service/ThoughtEventService.java`
- **Relevance:** Direct modification target — currently publishes raw Thought entities
- **Key patterns:** `MutinyEmitter<Thought>`, `sendAndAwait()`, separate methods per event type

### Current Kafka Consumer (ThoughtEvaluationConsumer)

- **Location:** `thoughts-evaluation/src/main/java/com/redhat/demos/evaluation/consumer/ThoughtEvaluationConsumer.java`
- **Relevance:** Direct modification target — currently receives ThoughtEvent DTOs
- **Key patterns:** `@Incoming`, `@Blocking`, event type filtering via DTO field, error resilience

## External References

- SmallRye Reactive Messaging CloudEvents support: built into `quarkus-messaging-kafka`
- CloudEvents spec v1.0: defines `specversion`, `id`, `type`, `source`, `subject`, `time`, `data`
