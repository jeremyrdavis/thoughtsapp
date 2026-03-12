package com.redhat.demos.thoughts.admin.resource;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/evaluations")
@Produces(MediaType.TEXT_HTML)
public class EvaluationResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance evaluations();
        public static native TemplateInstance stats();
    }

    @GET
    public TemplateInstance evaluations() {
        return Templates.evaluations();
    }

    @GET
    @Path("/stats")
    public TemplateInstance stats() {
        return Templates.stats();
    }
}
