package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.client.ThoughtBackendClient;
import com.redhat.demos.thoughts.admin.model.Thought;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Comparator;
import java.util.List;

@Path("/ratings")
@Produces(MediaType.TEXT_HTML)
public class RatingsResource {

    @Inject
    @RestClient
    ThoughtBackendClient backendClient;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance ratings(
                List<Thought> thoughts,
                String currentSort
        );
    }

    @GET
    public TemplateInstance ratings(@QueryParam("sort") @DefaultValue("most-rated") String sort) {
        List<Thought> thoughts = backendClient.list(0, 10000);

        Comparator<Thought> comparator = switch (sort) {
            case "most-liked" -> Comparator.comparingInt(Thought::thumbsUp).reversed();
            case "most-disliked" -> Comparator.comparingInt(Thought::thumbsDown).reversed();
            default -> Comparator.comparingInt((Thought t) -> t.thumbsUp() + t.thumbsDown()).reversed();
        };

        List<Thought> sorted = thoughts.stream().sorted(comparator).toList();

        return Templates.ratings(sorted, sort);
    }
}
