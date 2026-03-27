# Standards for CloudEvents Kafka Migration

The following standards apply to this work.

---

## backend/api

REST resources in ThoughtResource trigger Kafka events on create/update/delete. No API changes needed — only the internal event publishing mechanism changes.

---

## global/conventions

Follows existing Quarkus patterns:
- SmallRye Reactive Messaging for Kafka
- MicroProfile Reactive Messaging annotations (`@Channel`, `@Incoming`)
- Quarkus profile-based configuration (`%dev`, `%test`, `%prod`)
- In-memory connector for test isolation
