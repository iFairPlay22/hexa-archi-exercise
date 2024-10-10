package lunatech.application.server.resources.todos.requests;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;

import java.util.List;

public record CreateTodoRequest(
        @NotNull String title,
        @NotNull List<@NotNull String> tags,
        @NotNull Boolean done
) {
    public static TodoModelCreatePayload to(CreateTodoRequest model) {
        return new TodoModelCreatePayload(
                model.title(),
                model.tags(),
                model.done()
        );
    }
}