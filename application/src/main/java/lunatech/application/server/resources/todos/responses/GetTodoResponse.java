package lunatech.application.server.resources.todos.responses;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.models.TodoModel;
import org.bson.types.ObjectId;

import java.util.List;

public record GetTodoResponse(
        @NotNull ObjectId id,
        @NotNull String title,
        @NotNull List<@NotNull String> tags,
        @NotNull Boolean done
) {
    public static GetTodoResponse from(TodoModel model) {
        return new GetTodoResponse(
                model.id(),
                model.title(),
                model.tags(),
                model.done()
        );
    }
}
