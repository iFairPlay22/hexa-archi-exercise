package lunatech.application.server.resources.todos.requests;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;

import java.util.List;

public record UpdateTodoRequest(
        @NotNull String title,
        @NotNull List<@NotNull String> tags,
        @NotNull Boolean done
) {
    public static TodoModelUpdatePayload to(UpdateTodoRequest model) {
        return new TodoModelUpdatePayload(
                model.title(),
                model.tags(),
                model.done()
        );
    }
}