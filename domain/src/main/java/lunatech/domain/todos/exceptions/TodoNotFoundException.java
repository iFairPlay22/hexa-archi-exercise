package lunatech.domain.todos.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

@AllArgsConstructor
public final class TodoNotFoundException extends Exception {

    private final @NotNull String username;
    private final @NotNull ObjectId todoId;

    @Override
    public String getMessage() {
        return String.format("Todo %s was not found for user %s.", todoId, username);
    }
}
