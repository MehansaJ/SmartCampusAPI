package com.smartcampus.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse("Unprocessable Entity", exception.getMessage());
        return Response.status(422) // Unprocessable Entity
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
