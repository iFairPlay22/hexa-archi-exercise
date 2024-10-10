package lunatech.domain.todos.payloads;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.models.TodoModel;

import java.util.List;

public record UserWithTodosModelCreatePayload(
        @NotNull String username,
        @NotNull List<@NotNull TodoModel> todos
) {
}
