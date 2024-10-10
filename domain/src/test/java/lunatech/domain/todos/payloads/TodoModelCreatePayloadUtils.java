package lunatech.domain.todos.payloads;

import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;

public class TodoModelCreatePayloadUtils {
    public static TodoModelCreatePayload from(TodoModel todoModel) {
        return new TodoModelCreatePayload(
                todoModel.title(),
                todoModel.tags(),
                todoModel.done()
        );
    }
}