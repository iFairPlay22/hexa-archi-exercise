package lunatech.application.server.resources.todos.responses;

import lunatech.domain.todos.models.TodoModel;

import java.util.List;

public class GetTodosResponseUtils {
    public static List<TodoModel> from(GetTodosResponse response) {
        return response.todos().stream().map(GetTodoResponseUtils::from).toList();
    }
}
