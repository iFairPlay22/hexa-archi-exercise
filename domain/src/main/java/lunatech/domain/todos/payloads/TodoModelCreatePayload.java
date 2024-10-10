package lunatech.domain.todos.payloads;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TodoModelCreatePayload(
        @NotNull String title,
        @NotNull List<@NotNull String> tags,
        @NotNull Boolean done
) {
}