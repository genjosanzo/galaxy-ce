package org.mule.galaxy.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.mule.galaxy.security.AccessException;

@Provider
public class AccessExceptionMapper implements ExceptionMapper<AccessException> {

    public Response toResponse(AccessException e) {
        return error("User did not have permissions to access that resource or perform that operation (" + e.getMessage() + ")", Response.status(401));
    }

    protected Response error(String string, ResponseBuilder builder) {
        return builder.type("application/json").entity(new ErrorMessage(string)).build();
    }
}
