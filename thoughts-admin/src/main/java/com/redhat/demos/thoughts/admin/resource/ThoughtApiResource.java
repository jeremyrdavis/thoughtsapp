package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.client.ThoughtBackendClient;
import com.redhat.demos.thoughts.admin.model.Thought;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.UUID;

@Path("/api/thoughts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThoughtApiResource {

    @Inject
    @RestClient
    ThoughtBackendClient backendClient;

    @GET
    public List<Thought> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return backendClient.list(page, size);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        Thought thought = backendClient.get(id);
        return Response.ok(thought).build();
    }

    @POST
    public Response create(Thought thought) {
        Thought created = backendClient.create(thought);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, Thought updated) {
        Thought thought = backendClient.update(id, updated);
        return Response.ok(thought).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        backendClient.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/thumbsup/{id}")
    public Response thumbsUp(@PathParam("id") UUID id) {
        Thought thought = backendClient.thumbsUp(id);
        return Response.ok(thought).build();
    }

    @POST
    @Path("/thumbsdown/{id}")
    public Response thumbsDown(@PathParam("id") UUID id) {
        Thought thought = backendClient.thumbsDown(id);
        return Response.ok(thought).build();
    }
}
