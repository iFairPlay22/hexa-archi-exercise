package lunatech.application.server.resources.todos;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lunatech.application.server.resources.todos.requests.CreateTodoRequest;
import lunatech.application.server.resources.todos.requests.UpdateTodoRequest;
import lunatech.application.server.resources.todos.responses.GetTodoResponse;
import lunatech.application.server.resources.todos.responses.GetTodosResponse;
import lunatech.application.server.services.AuthQuarkusService;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.auth.models.AuthModel;
import lunatech.domain.todos.TodoServicePort;
import lunatech.domain.todos.exceptions.TodoNotFoundException;
import lunatech.domain.todos.exceptions.UserWithTodosNotFoundException;
import lunatech.domain.todos.models.TodoModel;
import org.bson.types.ObjectId;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Resource to manage todos lists of users
 * NB: Regular users are allowed to get/modify/delete their own todos
 * NB: Admin users are allowed to get/modify/delete every todos
 */
@Path("/api/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TodoResource {
    @Inject
    AuthQuarkusService authQuarkusService;
    @Inject
    TodoServicePort todoServicePort;

    @GET
    @RolesAllowed({AuthModel.Role.Names.ADMIN, AuthModel.Role.Names.REGULAR})
    public Response todos(
            @QueryParam("filter") Optional<String> filter,
            @QueryParam("user") Optional<String> userName
    ) throws UserWithTodosNotFoundException, UnauthorizedActionException {
        String userActor = authQuarkusService.userName();
        String userTarget = userName.orElse(userActor);

        List<TodoModel> todos =
                filter.isPresent() ?
                        todoServicePort.fetchTodosByFilter(userActor, userTarget, filter.get()) :
                        todoServicePort.fetchTodos(userActor, userTarget);

        GetTodosResponse response = GetTodosResponse.from(todos);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({AuthModel.Role.Names.ADMIN, AuthModel.Role.Names.REGULAR})
    public Response todo(
            @PathParam("id") ObjectId id,
            @QueryParam("user") Optional<String> userName
    ) throws UnauthorizedActionException, TodoNotFoundException {
        String userActor = authQuarkusService.userName();
        String userTarget = userName.orElse(userActor);

        TodoModel todo = todoServicePort.fetchTodoWithId(userActor, userTarget, id);

        GetTodoResponse response = GetTodoResponse.from(todo);
        return Response.ok(response).build();
    }

    @POST
    @RolesAllowed({AuthModel.Role.Names.ADMIN, AuthModel.Role.Names.REGULAR})
    public Response addTodo(
            @QueryParam("user") Optional<String> userName,
            @NotNull @Valid CreateTodoRequest todoToAdd
    ) throws UnauthorizedActionException {
        String userActor = authQuarkusService.userName();
        String userTarget = userName.orElse(userActor);

        TodoModel todo = todoServicePort.addTodo(userActor, userTarget, CreateTodoRequest.to(todoToAdd));

        URI uri = URI.create(String.format("/api/todos/%s", todo.id()));
        GetTodoResponse response = GetTodoResponse.from(todo);
        return Response.created(uri).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({AuthModel.Role.Names.ADMIN, AuthModel.Role.Names.REGULAR})
    public Response updateTodo(
            @PathParam("id") ObjectId id,
            @QueryParam("user") Optional<String> userName,
            @NotNull @Valid UpdateTodoRequest todoToUpdate
    ) throws UnauthorizedActionException, TodoNotFoundException {
        String userActor = authQuarkusService.userName();
        String userTarget = userName.orElse(userActor);

        TodoModel todo = todoServicePort.updateTodo(userActor, userTarget, id, UpdateTodoRequest.to(todoToUpdate));

        GetTodoResponse response = GetTodoResponse.from(todo);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({AuthModel.Role.Names.ADMIN, AuthModel.Role.Names.REGULAR})
    public Response deleteTodo(
            @PathParam("id") ObjectId id,
            @QueryParam("user") Optional<String> userName
    ) throws UnauthorizedActionException, TodoNotFoundException {
        String userActor = authQuarkusService.userName();
        String userTarget = userName.orElse(userActor);

        todoServicePort.removeTodo(userActor, userTarget, id);

        return Response.noContent().build();
    }
}

