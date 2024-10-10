package lunatech.domain.todos.payloads;

import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;


public class TodoModelUpdatePayloadUtils {
    public static TodoModelUpdatePayload from(TodoModel todoModel) {
        return new TodoModelUpdatePayload(
                todoModel.title(),
                todoModel.tags(),
                todoModel.done()
        );
    }
}
