package com.redhat.demos.thoughts.admin.client;

import com.redhat.demos.thoughts.admin.model.Thought;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.UUID;

@Path("/thoughts")
@RegisterRestClient
public interface ThoughtBackendClient {

    @GET
    List<Thought> list(
            @QueryParam("page") int page,
            @QueryParam("size") int size);

    @GET
    @Path("/{id}")
    Thought get(@PathParam("id") UUID id);

    @POST
    Thought create(Thought thought);

    @PUT
    @Path("/{id}")
    Thought update(@PathParam("id") UUID id, Thought thought);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") UUID id);

    @POST
    @Path("/thumbsup/{id}")
    Thought thumbsUp(@PathParam("id") UUID id);

    @POST
    @Path("/thumbsdown/{id}")
    Thought thumbsDown(@PathParam("id") UUID id);
}
