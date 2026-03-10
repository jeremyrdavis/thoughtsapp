-- Sample data for dev profile
-- Thoughts with varying statuses and ratings

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'The only way to do great work is to love what you do.', 'Steve Jobs', 'CEO and co-founder of Apple Inc.', 'APPROVED', 15, 2, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'In three words I can sum up everything I have learned about life: it goes on.', 'Robert Frost', 'poet', 'APPROVED', 22, 1, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Be yourself; everyone else is already taken.', 'Oscar Wilde', 'playwright', 'APPROVED', 30, 3, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 hours');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('d4e5f6a7-b8c9-0123-defa-234567890123', 'This thought is under review and has not been evaluated yet by the system.', 'Anonymous', 'Unknown', 'IN_REVIEW', 0, 0, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('e5f6a7b8-c9d0-1234-efab-345678901234', 'Sometimes negative thoughts creep in and that is perfectly normal and okay.', 'Jane Doe', 'wellness writer', 'REJECTED', 1, 10, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 hours');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('f6a7b8c9-d0e1-2345-fabc-456789012345', 'Every moment is a fresh beginning and a chance to start something wonderful.', 'T.S. Eliot', 'poet and playwright', 'APPROVED', 8, 0, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour');

INSERT INTO thoughts (id, content, author, author_bio, status, thumbs_up, thumbs_down, created_at, updated_at)
VALUES ('a7b8c9d0-e1f2-3456-abcd-567890123456', 'A new thought that is currently being reviewed by the moderation team.', 'New User', 'first-time contributor', 'IN_REVIEW', 0, 0, CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes');

-- Sample thought evaluations
INSERT INTO thought_evaluations (id, thought_id, status, similarity_score, evaluated_at, metadata)
VALUES ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'APPROVED', 0.2345, CURRENT_TIMESTAMP - INTERVAL '4 days', '{"model": "text-embedding-ada-002", "reason": "positive content"}');

INSERT INTO thought_evaluations (id, thought_id, status, similarity_score, evaluated_at, metadata)
VALUES ('22222222-2222-2222-2222-222222222222', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'APPROVED', 0.1890, CURRENT_TIMESTAMP - INTERVAL '3 days', '{"model": "text-embedding-ada-002", "reason": "inspirational content"}');

INSERT INTO thought_evaluations (id, thought_id, status, similarity_score, evaluated_at, metadata)
VALUES ('33333333-3333-3333-3333-333333333333', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'APPROVED', 0.3120, CURRENT_TIMESTAMP - INTERVAL '2 days', '{"model": "text-embedding-ada-002", "reason": "motivational content"}');

INSERT INTO thought_evaluations (id, thought_id, status, similarity_score, evaluated_at, metadata)
VALUES ('44444444-4444-4444-4444-444444444444', 'e5f6a7b8-c9d0-1234-efab-345678901234', 'REJECTED', 0.9150, CURRENT_TIMESTAMP - INTERVAL '1 day', '{"model": "text-embedding-ada-002", "reason": "high similarity to negative patterns"}');

INSERT INTO thought_evaluations (id, thought_id, status, similarity_score, evaluated_at, metadata)
VALUES ('55555555-5555-5555-5555-555555555555', 'f6a7b8c9-d0e1-2345-fabc-456789012345', 'APPROVED', 0.1560, CURRENT_TIMESTAMP - INTERVAL '5 hours', '{"model": "text-embedding-ada-002", "reason": "uplifting content"}');
