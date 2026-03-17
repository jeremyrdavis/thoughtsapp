package com.redhat.demos.evaluation.resource;

import com.redhat.demos.evaluation.dto.VectorInitializationResultDTO;
import com.redhat.demos.evaluation.dto.VectorStatusDTO;
import com.redhat.demos.evaluation.exception.ErrorResponse;
import com.redhat.demos.evaluation.service.VectorInitializationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/vectors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VectorResource {

    @Inject
    VectorInitializationService vectorInitializationService;

    @POST
    @Path("/initialize")
    public Response initializeVectors() {
        try {
            VectorInitializationResultDTO result = vectorInitializationService.initializeVectors();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to initialize vectors: " + e.getMessage(),
                            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @GET
    @Path("/status")
    public Response getVectorStatus() {
        VectorStatusDTO status = vectorInitializationService.getVectorStatus();
        return Response.ok(status).build();
    }
}
