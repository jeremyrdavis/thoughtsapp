# CloudEvents for Kafka Messages — Plan

## Overview

Migrate Kafka messaging from raw entity serialization to CloudEvents structured format using SmallRye Reactive Messaging's built-in support. No new dependencies required.

## Tasks

### Task 1: Save Spec Documentation
Create this spec folder with plan, shape, standards, and references.

### Task 2: Update Producer — ThoughtEventService
Attach `OutgoingCloudEventMetadata` with dynamic type, source, subject per message.

### Task 3: Update Producer application.properties
Switch to `StringSerializer`, add `cloud-events-mode=structured` and related config.

### Task 4: Update Producer Tests
Assert CloudEvents metadata (type, source, subject) on published messages.

### Task 5: Update Consumer — ThoughtEvaluationConsumer
Accept `Message<ThoughtEvent>`, extract `IncomingCloudEventMetadata` for type-based filtering.

### Task 6: Clean Up ThoughtEvent DTO and Delete Deserializer
Remove `eventType`/`timestamp` fields from DTO. Delete `ThoughtEventDeserializer`.

### Task 7: Update Consumer application.properties
Add `StringDeserializer`, remove commented deserializer line.

### Task 8: Update Consumer Tests
Update all 3 test files to use `Message<ThoughtEvent>` with mocked CE metadata.

### Task 9: Run All Tests and Verify
Run `mvnw test` in both services.

## Wire Format

```json
{
  "specversion": "1.0",
  "id": "<uuid>",
  "type": "com.redhat.demos.thoughts.created",
  "source": "/thoughts-backend",
  "subject": "<thought-uuid>",
  "time": "2026-03-23T12:00:00Z",
  "datacontenttype": "application/json",
  "data": { ... thought entity ... }
}
```
