package lunatech.application.server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.todos.exceptions.TodoAlreadyExistingException;
import lunatech.domain.todos.exceptions.TodoNotFoundException;
import lunatech.domain.todos.exceptions.UserWithTodosNotFoundException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Provider
public class HttpServerExceptionHandler {
    @ServerExceptionMapper
    public Response handle(UnauthorizedActionException e) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    }

    @ServerExceptionMapper
    public Response handle(TodoAlreadyExistingException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }

    @ServerExceptionMapper
    public Response handle(TodoNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }

    @ServerExceptionMapper
    public Response handle(UserWithTodosNotFoundException e) {
        return Response.noContent().entity(e.getMessage()).build();
    }

}
