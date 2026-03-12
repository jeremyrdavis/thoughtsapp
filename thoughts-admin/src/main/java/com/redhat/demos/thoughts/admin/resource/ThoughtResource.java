package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.client.ThoughtBackendClient;
import com.redhat.demos.thoughts.admin.model.Thought;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.*;

@Path("/thoughts")
@Produces(MediaType.TEXT_HTML)
public class ThoughtResource {

    @Inject
    @RestClient
    ThoughtBackendClient backendClient;

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

        public static native TemplateInstance detail(Thought thought);

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

        List<Thought> thoughtList = backendClient.list(page, size);

        List<Thought> allThoughts = backendClient.list(0, 10000);
        long totalCount = allThoughts.size();
        boolean hasMore = (long) (page + 1) * size < totalCount;

        return Templates.thoughts(thoughtList, page, size, hasMore, totalCount);
    }

    @GET
    @Path("/{id}")
    public TemplateInstance detail(@PathParam("id") UUID id) {
        Thought thought = backendClient.get(id);
        return Templates.detail(thought);
    }

    @GET
    @Path("/create")
    public TemplateInstance createForm() {
        return Templates.create("", "", "", Map.of());
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Object createThought(
            @FormParam("content") String content,
            @FormParam("author") String author,
            @FormParam("authorBio") String authorBio) {

        Thought thought = new Thought(null, content, 0, 0, null, author, authorBio, null, null);

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

        Thought created = backendClient.create(thought);
        return Response.seeOther(URI.create("/thoughts/" + created.id())).build();
    }

    @GET
    @Path("/{id}/edit")
    public TemplateInstance editForm(@PathParam("id") UUID id) {
        Thought thought = backendClient.get(id);
        return Templates.edit(thought, Map.of());
    }

    @POST
    @Path("/{id}/edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Object editThought(
            @PathParam("id") UUID id,
            @FormParam("content") String content,
            @FormParam("author") String author,
            @FormParam("authorBio") String authorBio) {

        Thought thought = new Thought(id, content, 0, 0, null, author, authorBio, null, null);

        Set<ConstraintViolation<Thought>> violations = validator.validate(thought);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<Thought> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return Templates.edit(thought, errors);
        }

        backendClient.update(id, thought);
        return Response.seeOther(URI.create("/thoughts/" + id)).build();
    }

    @POST
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") UUID id) {
        backendClient.delete(id);
        return Response.seeOther(URI.create("/thoughts")).build();
    }
}
