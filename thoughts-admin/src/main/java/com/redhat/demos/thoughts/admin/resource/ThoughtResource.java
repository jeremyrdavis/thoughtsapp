package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.model.Thought;
import com.redhat.demos.thoughts.admin.model.ThoughtEvaluation;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.*;

@Path("/thoughts")
@Produces(MediaType.TEXT_HTML)
public class ThoughtResource {

    @Inject
    Validator validator;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance thoughts(
                List<Thought> thoughts,
                int page,
                int size,
                boolean hasMore,
                long totalCount
        );

        public static native TemplateInstance detail(
                Thought thought,
                List<ThoughtEvaluation> evaluations
        );

        public static native TemplateInstance create(
                String content,
                String author,
                String authorBio,
                Map<String, String> errors
        );

        public static native TemplateInstance edit(
                Thought thought,
                Map<String, String> errors
        );
    }

    @GET
    public TemplateInstance list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        List<Thought> thoughtList = Thought.find("ORDER BY createdAt DESC")
                .page(page, size)
                .list();

        long totalCount = Thought.count();
        boolean hasMore = (long) (page + 1) * size < totalCount;

        return Templates.thoughts(thoughtList, page, size, hasMore, totalCount);
    }

    @GET
    @Path("/{id}")
    public TemplateInstance detail(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            throw new WebApplicationException("Thought not found", Response.Status.NOT_FOUND);
        }

        List<ThoughtEvaluation> evaluations = ThoughtEvaluation.find(
                "thoughtId", id).list();

        return Templates.detail(thought, evaluations);
    }

    @GET
    @Path("/create")
    public TemplateInstance createForm() {
        return Templates.create("", "", "", Map.of());
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Object createThought(
            @FormParam("content") String content,
            @FormParam("author") String author,
            @FormParam("authorBio") String authorBio) {

        Thought thought = new Thought();
        thought.content = content;
        thought.author = author;
        thought.authorBio = authorBio;

        Set<ConstraintViolation<Thought>> violations = validator.validate(thought);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<Thought> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return Templates.create(
                    content != null ? content : "",
                    author != null ? author : "",
                    authorBio != null ? authorBio : "",
                    errors
            );
        }

        thought.persist();
        return Response.seeOther(URI.create("/thoughts/" + thought.id)).build();
    }

    @GET
    @Path("/{id}/edit")
    public TemplateInstance editForm(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            throw new WebApplicationException("Thought not found", Response.Status.NOT_FOUND);
        }
        return Templates.edit(thought, Map.of());
    }

    @POST
    @Path("/{id}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Object editThought(
            @PathParam("id") UUID id,
            @FormParam("content") String content,
            @FormParam("author") String author,
            @FormParam("authorBio") String authorBio) {

        Thought thought = Thought.findById(id);
        if (thought == null) {
            throw new WebApplicationException("Thought not found", Response.Status.NOT_FOUND);
        }

        thought.content = content;
        thought.author = author;
        thought.authorBio = authorBio;

        Set<ConstraintViolation<Thought>> violations = validator.validate(thought);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<Thought> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return Templates.edit(thought, errors);
        }

        thought.persist();
        return Response.seeOther(URI.create("/thoughts/" + thought.id)).build();
    }

    @POST
    @Path("/{id}/delete")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            throw new WebApplicationException("Thought not found", Response.Status.NOT_FOUND);
        }

        ThoughtEvaluation.delete("thoughtId", id);
        thought.delete();
        return Response.seeOther(URI.create("/thoughts")).build();
    }
}
