package lunatech.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import lunatech.entities.TodoEntity;
import lunatech.entities.UserEntity;
import lunatech.exceptions.InvalidTodoFormatException;
import lunatech.exceptions.TodoAlreadyExistingException;
import lunatech.exceptions.TodoNotFoundException;
import lunatech.exceptions.UnauthorizedActionException;
import lunatech.security.Action;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject Validator validator;
    @Inject AuthService authService;

    public UserEntity queryUser(
            @NotNull String userName
    ) throws UnauthorizedActionException {
        authService.failIfUnauthorizedAccess(Action.GET, userName);
        return queryUser(userName, new Document());
    }

    public UserEntity queryUser(
            @NotNull String userName,
            @NotNull Document filters
    ) throws UnauthorizedActionException {
        authService.failIfUnauthorizedAccess(Action.GET, userName);

        Document allFilters = new Document("$and", List.of(filters, UserService.UserFilters.userName(userName)));
        Optional<UserEntity> maybeUser = UserEntity.find(allFilters)
                .range(0, 1)
                .firstResultOptional();

        return maybeUser.orElseThrow(UnauthorizedActionException::new);
    }


    /**
     * Utility function to add a new element to the user todos
     *
     * @param user      User target
     * @param todoToAdd Element to add
     */
    public void addTodo(
            @NotNull UserEntity user,
            @NotNull TodoEntity todoToAdd
    ) throws UnauthorizedActionException, TodoAlreadyExistingException, InvalidTodoFormatException {
        authService.failIfUnauthorizedAccess(Action.POST, user.username);

        boolean todoAlreadyExists = user.todos.stream().anyMatch(t -> t.todoId.equals(todoToAdd.todoId));
        if (todoAlreadyExists) throw new TodoAlreadyExistingException();

        var violations = validator.validate(todoToAdd);
        if (!violations.isEmpty()) throw new InvalidTodoFormatException(violations.stream().map(ConstraintViolation::getMessage).toList());

        user.todos.add(todoToAdd);
    }

    /**
     * Utility function to update an element to the user todos
     *
     * @param user         User target
     * @param todoToUpdate Element to update
     */
    public void updateTodo(
            @NotNull UserEntity user,
            @NotNull TodoEntity todoToUpdate
    ) throws UnauthorizedActionException, TodoNotFoundException, InvalidTodoFormatException {
        authService.failIfUnauthorizedAccess(Action.PUT, user.username);

        boolean todoAlreadyExists = user.todos.stream().anyMatch(t -> t.todoId.equals(todoToUpdate.todoId));
        if (!todoAlreadyExists) throw new TodoNotFoundException();

        var violations = validator.validate(todoToUpdate);
        if (!violations.isEmpty()) throw new InvalidTodoFormatException(violations.stream().map(ConstraintViolation::getMessage).toList());

        user.todos = user.todos
                .stream()
                .map(t -> t.todoId.equals(todoToUpdate.todoId) ? todoToUpdate : t)
                .toList();
    }

    /**
     * Utility function to remove an element from the user todos
     *
     * @param user           User target
     * @param todoIdToRemove element to remove
     */
    public void removeTodo(
            @NotNull UserEntity user,
            @NotNull ObjectId todoIdToRemove
    ) throws UnauthorizedActionException, TodoNotFoundException {
        authService.failIfUnauthorizedAccess(Action.DELETE, user.username);

        boolean todoAlreadyExists = user.todos.stream().anyMatch(t -> t.todoId.equals(todoIdToRemove));
        if (!todoAlreadyExists) throw new TodoNotFoundException();

        user.todos = user.todos
            .stream()
            .filter(t -> !t.todoId.equals(todoIdToRemove))
            .toList();
    }

    public static class UserFilters {
        /**
         * Filter by username
         */
        public static Document userName(@NotNull String userName) {
            return new Document("username", new Document("$eq", userName));
        }

        /**
         * Filter by todo_ tags
         */
        public static Document todoTags(@NotNull List<String> tags) {
            return new Document("todos", new Document("$elemMatch", new Document("tags", new Document("$all", tags))));
        }
    }
}
