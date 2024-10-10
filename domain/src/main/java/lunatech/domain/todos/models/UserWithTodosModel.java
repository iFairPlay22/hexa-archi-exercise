package lunatech.domain.todos.models;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lunatech.domain.todos.payloads.UserWithTodosModelCreatePayload;
import lunatech.domain.todos.payloads.UserWithTodosModelUpdatePayload;

import java.util.List;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public record UserWithTodosModel(

        @NotNull(message = "Username should be set")
        String username,

        @NotNull(message = "Todos should be set")
        List<@NotNull(message = "Todos.* should not be null") TodoModel> todos

) {

    public static UserWithTodosModel createFrom(UserWithTodosModelCreatePayload payload) {
        return builder()
                .username(payload.username())
                .todos(payload.todos())
                .build();
    }

    public UserWithTodosModel updateFrom(UserWithTodosModelUpdatePayload payload) {
        return toBuilder()
                .todos(payload.todos())
                .build();
    }

}