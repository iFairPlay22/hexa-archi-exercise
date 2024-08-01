package lunatech.resources;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.net.URI;

import jakarta.annotation.security.RolesAllowed;
import lunatech.exceptions.InvalidTodoFormatException;
import lunatech.exceptions.TodoAlreadyExistingException;
import lunatech.exceptions.TodoNotFoundException;
import lunatech.exceptions.UnauthorizedActionException;
import lunatech.security.Role;
import lunatech.entities.TodoEntity;
import lunatech.entities.UserEntity;
import lunatech.services.AuthService;
import lunatech.services.UserService;
import lunatech.services.UserService.UserFilters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * CRUD for TodoEntity
 * NB: todos are stored in users
 * NB: Regular users are allowed to get/modify/delete their own todos
 * NB: Admin users are allowed to get/modify/delete every todos
 */
@Path("/api/todos")
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @Inject AuthService authService;
    @Inject UserService userService;

    @GET
    @RolesAllowed({ Role.Names.ADMIN, Role.Names.REGULAR })
    public Response todos(
            @QueryParam("tags") Optional<String> tagsFilter,
            @QueryParam("user") Optional<String> userName
    ) {
        try {

            String userTarget =  userName.orElse(authService.userName());
            Document filters = tagsFilter
                    .map(tags -> {
                        var tagsList = Arrays.stream(tagsFilter.get().split(",")).toList();
                        return UserFilters.todoTags(tagsList);
                    })
                    .orElseGet(Document::new);
            UserEntity user = userService.queryUser(userTarget, filters);
            List<TodoEntity> todos = user.todos;
            return Response.ok(todos).build();

        } catch (UnauthorizedActionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ Role.Names.ADMIN, Role.Names.REGULAR })
    public Response todo(
            @PathParam("id") ObjectId id,
            @QueryParam("user") Optional<String> userName
    ) {
        try {

            String userTarget =  userName.orElse(authService.userName());
            UserEntity user = userService.queryUser(userTarget);
            TodoEntity todo = user.todos.stream()
                    .filter(e -> e.todoId.equals(id))
                    .findFirst()
                    .orElseThrow(TodoNotFoundException::new);
            return Response.ok(todo).build();

        } catch (UnauthorizedActionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (TodoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ Role.Names.ADMIN, Role.Names.REGULAR })
    public Response deleteTodo(
            @PathParam("id") ObjectId id,
            @QueryParam("user") Optional<String> userName
        ) {
        try {

            String userTarget =  userName.orElse(authService.userName());
            UserEntity user = userService.queryUser(userTarget);
            userService.removeTodo(user, id);
            user.persistOrUpdate();
            return Response.noContent().build();

        } catch (UnauthorizedActionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (TodoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @RolesAllowed({ Role.Names.ADMIN, Role.Names.REGULAR })
    public Response addTodo(
            @QueryParam("user") Optional<String> userName,
            TodoEntity todoToAdd
    ) {
       try {

           String userTarget =  userName.orElse(authService.userName());
           UserEntity user = userService.queryUser(userTarget);
           userService.addTodo(user, todoToAdd);
           user.persistOrUpdate();
           URI uri = URI.create(String.format("/api/todos/%s", todoToAdd.todoId));
           return Response.created(uri).entity(todoToAdd).build();

       } catch (UnauthorizedActionException e) {
           return Response.status(Response.Status.UNAUTHORIZED).build();
       } catch (TodoAlreadyExistingException e) {
           return Response.status(Response.Status.BAD_REQUEST).build();
       } catch (InvalidTodoFormatException e) {
           return Response.status(Response.Status.BAD_REQUEST).entity(e.getViolations()).build();
       }
    }

    @PUT
    @RolesAllowed({ Role.Names.ADMIN, Role.Names.REGULAR })
    public Response updateTodo(
            @QueryParam("user") Optional<String> userName,
            TodoEntity todoToUpdate
    ) {
        try {

            String userTarget =  userName.orElse(authService.userName());
            UserEntity user = userService.queryUser(userTarget);
            userService.updateTodo(user, todoToUpdate);
            user.persistOrUpdate();
            return Response.ok().build();

        } catch (UnauthorizedActionException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (TodoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (InvalidTodoFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getViolations()).build();
        }
    }
}

