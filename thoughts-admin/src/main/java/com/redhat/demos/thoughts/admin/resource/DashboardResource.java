package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.model.Thought;
import com.redhat.demos.thoughts.admin.model.ThoughtStatus;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance dashboard(
                long totalThoughts,
                long totalThumbsUp,
                long totalThumbsDown,
                long approvedCount,
                long rejectedCount,
                long inReviewCount,
                List<Thought> recentThoughts
        );
    }

    @GET
    public TemplateInstance dashboard() {
        long totalThoughts = Thought.count();

        Long thumbsUpSum = Thought.getEntityManager()
                .createQuery("SELECT COALESCE(SUM(t.thumbsUp), 0) FROM Thought t", Long.class)
                .getSingleResult();
        Long thumbsDownSum = Thought.getEntityManager()
                .createQuery("SELECT COALESCE(SUM(t.thumbsDown), 0) FROM Thought t", Long.class)
                .getSingleResult();

        long approvedCount = Thought.count("status", ThoughtStatus.APPROVED);
        long rejectedCount = Thought.count("status", ThoughtStatus.REJECTED);
        long inReviewCount = Thought.count("status", ThoughtStatus.IN_REVIEW);

        List<Thought> recentThoughts = Thought.find("ORDER BY updatedAt DESC")
                .page(0, 5)
                .list();

        return Templates.dashboard(
                totalThoughts,
                thumbsUpSum,
                thumbsDownSum,
                approvedCount,
                rejectedCount,
                inReviewCount,
                recentThoughts
        );
    }
}
