package lunatech.domain.todos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.todos.exceptions.TodoNotFoundException;
import lunatech.domain.todos.exceptions.UserWithTodosNotFoundException;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Port that allows to send requests to the domain, related to _todos management
 */
public interface TodoServicePort {

    /**
     * Request to retrieve all the todos of a user
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @return The todos of the targeted user
     * @throws UnauthorizedActionException    If the actor is not authorized to do this action
     * @throws UserWithTodosNotFoundException If the targeted user is not found
     */
    @NotNull
    List<@NotNull TodoModel> fetchTodos(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName
    ) throws UnauthorizedActionException, UserWithTodosNotFoundException;

    /**
     * Request to retrieve all the todos of a user, that are matching with a filter
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @param filter         Filter to apply (considered as regexp in all the todos fields)
     * @return An array with all matching todos of the user
     * @throws UnauthorizedActionException    If the actor is not authorized to do this action
     * @throws UserWithTodosNotFoundException If the targeted user is not found
     */
    @NotNull
    List<@NotNull TodoModel> fetchTodosByFilter(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull String filter
    ) throws UnauthorizedActionException, UserWithTodosNotFoundException;

    /**
     * Request to retrieve a specific _todo of a user, given its id
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @param todoId         _Todo identifier
     * @return The corresponding _todo
     * @throws UnauthorizedActionException If the actor is not authorized to do this action
     * @throws TodoNotFoundException       If either the user or the _todo is not found
     */
    @NotNull
    TodoModel fetchTodoWithId(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId
    ) throws UnauthorizedActionException, TodoNotFoundException;

    /**
     * Adds a _todo
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @param payload        Fields to modify
     * @throws UnauthorizedActionException If the actor is not authorized to do this action
     */
    @NotNull
    TodoModel addTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull TodoModelCreatePayload payload
    ) throws UnauthorizedActionException;

    /**
     * Updates a _todo
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @param todoId         Identifier of the _todo to update
     * @param payload        Fields to create
     * @throws UnauthorizedActionException If the actor is not authorized to do this action
     * @throws TodoNotFoundException       If the _todo does not exist
     */
    @NotNull
    TodoModel updateTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId, @Valid @NotNull TodoModelUpdatePayload payload
    ) throws UnauthorizedActionException, TodoNotFoundException;

    /**
     * Removes a _todo
     *
     * @param actorUserName  Username of the person that do the request
     * @param targetUserName Username of the targeted user
     * @param todoId         _Todo id to remove
     * @throws UnauthorizedActionException If the actor is not authorized to do this action
     * @throws TodoNotFoundException       If the _todo does not exist
     */
    void removeTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId
    ) throws UnauthorizedActionException, TodoNotFoundException;
}
