package lunatech.domain.todos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lunatech.domain.auth.AuthServicePort;
import lunatech.domain.auth.exceptions.UnauthorizedActionException;
import lunatech.domain.auth.models.AuthModel.Role.Action;
import lunatech.domain.todos.exceptions.TodoNotFoundException;
import lunatech.domain.todos.exceptions.UserWithTodosNotFoundException;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;
import lunatech.domain.todos.payloads.UserWithTodosModelCreatePayload;
import lunatech.domain.todos.payloads.UserWithTodosModelUpdatePayload;
import org.bson.types.ObjectId;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor
public class TodoServiceAdapter implements TodoServicePort {
    private final AuthServicePort authService;
    private final TodoRepositoryPort todoRepository;

    @Override
    public @NotNull List<@NotNull TodoModel> fetchTodos(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName
    ) throws UnauthorizedActionException, UserWithTodosNotFoundException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        return todoRepository.findByUserName(targetUserName)
                .orElseThrow(() -> new UserWithTodosNotFoundException(targetUserName))
                .todos();
    }

    @Override
    public @NotNull List<@NotNull TodoModel> fetchTodosByFilter(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull String filter
    ) throws UnauthorizedActionException, UserWithTodosNotFoundException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        Function<String, String> cleanStringFn = (str) -> Normalizer.normalize(str, Normalizer.Form.NFKD).toLowerCase();
        String cleanedFilter = cleanStringFn.apply(filter);

        return fetchTodos(actorUserName, targetUserName)
                .stream()
                .filter(todo ->
                        new ArrayList<String>() {{
                            add(todo.id().toString());
                            add(todo.title());
                            addAll(todo.tags());
                        }}
                                .stream()
                                .map(cleanStringFn)
                                .anyMatch(fieldValue -> fieldValue.contains(cleanedFilter))
                )
                .toList();
    }

    @Override
    public @NotNull TodoModel fetchTodoWithId(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId
    ) throws UnauthorizedActionException, TodoNotFoundException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        return todoRepository.findByUserNameAndId(targetUserName, todoId)
                .orElseThrow(() -> new TodoNotFoundException(targetUserName, todoId));
    }

    @Override
    public @NotNull TodoModel addTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull TodoModelCreatePayload payload
    ) throws UnauthorizedActionException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        UserWithTodosModel targetUserWithTodos = todoRepository.findByUserName(targetUserName)
                .orElseGet(() -> UserWithTodosModel.createFrom(new UserWithTodosModelCreatePayload(targetUserName, List.of())));

        TodoModel createdTodo = TodoModel.createFrom(payload);

        UserWithTodosModel updatedUserWithTodos = targetUserWithTodos.updateFrom(
                new UserWithTodosModelUpdatePayload(
                        Stream.concat(targetUserWithTodos.todos().stream(), Stream.of(createdTodo)).toList()
                )
        );

        todoRepository.save(updatedUserWithTodos);

        return createdTodo;
    }

    @Override
    public @NotNull TodoModel updateTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId, @Valid @NotNull TodoModelUpdatePayload payload
    ) throws UnauthorizedActionException, TodoNotFoundException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        UserWithTodosModel targetUserWithTodos = todoRepository.findByUserName(targetUserName)
                .orElseThrow(() -> new TodoNotFoundException(targetUserName, todoId));

        TodoModel todo = targetUserWithTodos.todos().stream().filter(t -> t.id().equals(todoId)).findFirst()
                .orElseThrow(() -> new TodoNotFoundException(targetUserName, todoId));

        TodoModel updatedTodo = todo.updateFrom(payload);

        UserWithTodosModel updatedUserWithTodos = targetUserWithTodos.updateFrom(
                new UserWithTodosModelUpdatePayload(
                        Stream.concat(
                                targetUserWithTodos.todos().stream().filter(t -> !t.id().equals(updatedTodo.id())),
                                Stream.of(updatedTodo)
                        ).toList()
                )
        );

        todoRepository.save(updatedUserWithTodos);

        return updatedTodo;
    }

    @Override
    public void removeTodo(
            @Valid @NotNull String actorUserName, @Valid @NotNull String targetUserName, @Valid @NotNull ObjectId todoId
    ) throws UnauthorizedActionException, TodoNotFoundException {

        authService.canAccessTo(actorUserName, targetUserName, Action.VISUALIZE);

        UserWithTodosModel targetUserWithTodos = todoRepository.findByUserName(targetUserName)
                .orElseThrow(() -> new TodoNotFoundException(targetUserName, todoId));

        if (targetUserWithTodos.todos().stream().noneMatch(t -> t.id().equals(todoId)))
            throw new TodoNotFoundException(targetUserName, todoId);

        UserWithTodosModel updatedUserWithTodos = targetUserWithTodos.updateFrom(
                new UserWithTodosModelUpdatePayload(
                        targetUserWithTodos.todos().stream().filter(t -> !t.id().equals(todoId)).toList()
                )
        );

        todoRepository.save(updatedUserWithTodos);
    }
}
