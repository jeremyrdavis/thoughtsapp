# CloudEvents for Kafka Messages — Shaping Notes

## Scope

Migrate all Kafka messages across thoughts-backend (producer) and thoughts-evaluation (consumer) to use the CloudEvents specification via SmallRye Reactive Messaging's built-in support. Structured mode (`application/cloudevents+json`) with dynamic event types per operation.

## Decisions

- **SmallRye built-in approach** — No new dependencies; uses `OutgoingCloudEventMetadata` / `IncomingCloudEventMetadata` from `quarkus-messaging-kafka`
- **Structured mode** — Entire CloudEvents envelope in the Kafka message value (not binary mode with headers)
- **Dynamic event types** — `com.redhat.demos.thoughts.created`, `.updated`, `.deleted`
- **Source** — `/thoughts-backend` identifies the producing service
- **Subject** — Thought UUID for per-entity routing/filtering
- **StringSerializer/Deserializer** — SmallRye handles JSON serialization of the CloudEvents envelope
- **Delete ThoughtEventDeserializer** — No longer needed with structured mode
- **Remove eventType/timestamp from ThoughtEvent DTO** — Metadata moves to CloudEvents envelope

## Context

- **Visuals:** None
- **References:** No existing CloudEvents usage in codebase; SmallRye Reactive Messaging docs
- **Product alignment:** Enhances the event-driven architecture demo story — positions CloudEvents as a best practice for enterprise interoperability

## Standards Applied

- backend/api — REST resources trigger Kafka events; no API changes needed
- global/conventions — Follows existing Quarkus patterns (SmallRye, MicroProfile)
