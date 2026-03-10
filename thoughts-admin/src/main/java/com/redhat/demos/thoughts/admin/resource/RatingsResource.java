package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.model.Thought;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/ratings")
@Produces(MediaType.TEXT_HTML)
public class RatingsResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance ratings(
                List<Thought> thoughts,
                String currentSort
        );
    }

    @GET
    public TemplateInstance ratings(@QueryParam("sort") @DefaultValue("most-rated") String sort) {
        String orderClause = switch (sort) {
            case "most-liked" -> "ORDER BY thumbsUp DESC";
            case "most-disliked" -> "ORDER BY thumbsDown DESC";
            default -> "ORDER BY (thumbsUp + thumbsDown) DESC";
        };

        List<Thought> thoughts = Thought.find(orderClause).list();

        return Templates.ratings(thoughts, sort);
    }
}
