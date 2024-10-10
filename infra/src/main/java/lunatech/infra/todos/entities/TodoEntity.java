package lunatech.infra.todos.entities;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lunatech.domain.todos.models.TodoModel;
import org.bson.types.ObjectId;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class TodoEntity {
    private @NotNull ObjectId id;
    private @NotNull String title;
    private @NotNull List<@NotNull String> tags;
    private @NotNull Boolean done;

    public static TodoEntity from(TodoModel model) {
        return new TodoEntity(
                model.id(),
                model.title(),
                model.tags(),
                model.done()
        );
    }

    public static TodoModel to(TodoEntity entity) {
        return new TodoModel(
                entity.getId(),
                entity.getTitle(),
                entity.getTags(),
                entity.getDone()
        );
    }
}
