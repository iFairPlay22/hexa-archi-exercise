package lunatech.domain.todos.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserWithTodosNotFoundException extends Exception {

    private final @NotNull String username;

    @Override
    public String getMessage() {
        return String.format("Todo user %s does not exist.", username);
    }
}
