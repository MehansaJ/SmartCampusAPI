package com.smartcampus.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Task 5.4: Update Global Safety Net
        // Ignore WebApplicationException and custom exceptions so their specific status
        // codes reach Postman
        if (exception instanceof jakarta.ws.rs.WebApplicationException) {
            return ((jakarta.ws.rs.WebApplicationException) exception).getResponse();
        }

        // Manual routing for custom exceptions if they are somehow caught by this
        // global mapper
        if (exception instanceof RoomNotEmptyException) {
            return new RoomNotEmptyMapper().toResponse((RoomNotEmptyException) exception);
        }
        if (exception instanceof LinkedResourceNotFoundException) {
            return new LinkedResourceNotFoundMapper().toResponse((LinkedResourceNotFoundException) exception);
        }
        if (exception instanceof SensorUnavailableException) {
            return new SensorUnavailableMapper().toResponse((SensorUnavailableException) exception);
        }

        // Obfuscate internal error details for security (Cybersecurity Task)
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error",
                "An unexpected error occurred. Please contact support.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
