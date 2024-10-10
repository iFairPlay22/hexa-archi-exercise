package lunatech.application.server.resources.todos.responses;

import lunatech.domain.todos.models.TodoModel;

public class GetTodoResponseUtils {
    public static TodoModel from(GetTodoResponse response) {
        return new TodoModel(
                response.id(),
                response.title(),
                response.tags(),
                response.done()
        );
    }
}
