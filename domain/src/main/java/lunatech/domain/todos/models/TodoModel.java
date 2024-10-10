package lunatech.domain.todos.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lunatech.domain.todos.payloads.TodoModelCreatePayload;
import lunatech.domain.todos.payloads.TodoModelUpdatePayload;
import org.bson.types.ObjectId;

import java.util.List;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public record TodoModel(

        @NotNull(message = "Todo id should be set")
        ObjectId id,

        @NotNull(message = "Title should be set")
        @Size(min = 3, max = 30, message = "Title length should be between 3 and 30 characters")
        String title,

        @NotNull(message = "Tags should be set")
        List<@NotNull(message = "Tag should be set") String> tags,

        @NotNull(message = "Done should be set")
        Boolean done

) {

    public static TodoModel createFrom(TodoModelCreatePayload payload) {
        return builder()
                .id(new ObjectId())
                .title(payload.title())
                .tags(payload.tags())
                .done(payload.done())
                .build();
    }

    public TodoModel updateFrom(TodoModelUpdatePayload payload) {
        return toBuilder()
                .title(payload.title())
                .tags(payload.tags())
                .done(payload.done())
                .build();
    }

}
