package lunatech.application.server.resources.todos.responses;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.models.TodoModel;

import java.util.List;

public record GetTodosResponse(
        @NotNull List<@NotNull GetTodoResponse> todos
) {
    public static GetTodosResponse from(List<TodoModel> model) {
        return new GetTodosResponse(
                model.stream().map(GetTodoResponse::from).toList()
        );
    }
}
