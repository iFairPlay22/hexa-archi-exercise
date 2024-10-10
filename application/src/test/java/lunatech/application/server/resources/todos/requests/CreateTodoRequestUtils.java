package lunatech.application.server.resources.todos.requests;

import lunatech.domain.todos.models.TodoModel;

public class CreateTodoRequestUtils {
    public static CreateTodoRequest from(TodoModel payload) {
        return new CreateTodoRequest(
                payload.title(),
                payload.tags(),
                payload.done()
        );
    }
}