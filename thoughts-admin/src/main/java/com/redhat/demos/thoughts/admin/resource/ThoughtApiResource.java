package com.redhat.demos.thoughts.admin.resource;

import com.redhat.demos.thoughts.admin.model.Thought;
import com.redhat.demos.thoughts.admin.model.ThoughtEvaluation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/thoughts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThoughtApiResource {

    @GET
    public List<Thought> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return Thought.find("ORDER BY createdAt DESC")
                .page(page, size)
                .list();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(thought).build();
    }

    @POST
    @Transactional
    public Response create(@Valid Thought thought) {
        thought.persist();
        return Response.status(Response.Status.CREATED).entity(thought).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, Thought updated) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        thought.content = updated.content;
        if (updated.author != null) {
            thought.author = updated.author;
        }
        if (updated.authorBio != null) {
            thought.authorBio = updated.authorBio;
        }
        if (updated.status != null) {
            thought.status = updated.status;
        }
        thought.persist();
        return Response.ok(thought).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ThoughtEvaluation.delete("thoughtId", id);
        thought.delete();
        return Response.noContent().build();
    }

    @POST
    @Path("/thumbsup/{id}")
    @Transactional
    public Response thumbsUp(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        thought.thumbsUp++;
        thought.persist();
        return Response.ok(thought).build();
    }

    @POST
    @Path("/thumbsdown/{id}")
    @Transactional
    public Response thumbsDown(@PathParam("id") UUID id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        thought.thumbsDown++;
        thought.persist();
        return Response.ok(thought).build();
    }
}
