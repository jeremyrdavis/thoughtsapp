package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.client.ThoughtBackendClient;
import com.redhat.demos.thoughts.admin.model.Thought;
import com.redhat.demos.thoughts.admin.model.ThoughtStatus;
import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Comparator;
import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    @RestClient
    ThoughtBackendClient backendClient;

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
        List<Thought> all = backendClient.list(0, 10000);

        long totalThoughts = all.size();
        Log.debugf("Thoughts: %d", totalThoughts);
        long totalThumbsUp = all.stream().mapToInt(Thought::thumbsUp).sum();
        Log.debugf("ThumbsUp: %d", totalThumbsUp);
        long totalThumbsDown = all.stream().mapToInt(Thought::thumbsDown).sum();
        Log.debugf("ThumbsDown: %d", totalThumbsDown);
        long approvedCount = all.stream().filter(t -> t.status() == ThoughtStatus.APPROVED).count();
        Log.debugf("Approved: %d", approvedCount);
        long rejectedCount = all.stream().filter(t -> t.status() == ThoughtStatus.REJECTED).count();
        Log.debugf("Rejected: %d", rejectedCount);
        long inReviewCount = all.stream().filter(t -> t.status() == ThoughtStatus.IN_REVIEW).count();
        Log.debugf("InReview: %d", inReviewCount);

        List<Thought> recentThoughts = all.stream()
                .sorted(Comparator.comparing(Thought::updatedAt).reversed())
                .limit(5)
                .toList();

        return Templates.dashboard(
                totalThoughts,
                totalThumbsUp,
                totalThumbsDown,
                approvedCount,
                rejectedCount,
                inReviewCount,
                recentThoughts
        );
    }
}
