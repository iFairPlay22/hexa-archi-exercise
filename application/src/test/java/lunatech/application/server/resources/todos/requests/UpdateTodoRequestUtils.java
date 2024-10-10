package lunatech.application.server.resources.todos.requests;

import lunatech.domain.todos.models.TodoModel;

public class UpdateTodoRequestUtils {
    public static UpdateTodoRequest from(TodoModel todoModel) {
        return new UpdateTodoRequest(
                todoModel.title(),
                todoModel.tags(),
                todoModel.done()
        );
    }
}