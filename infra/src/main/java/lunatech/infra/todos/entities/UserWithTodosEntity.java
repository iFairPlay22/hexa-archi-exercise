package lunatech.infra.todos.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lunatech.domain.todos.models.UserWithTodosModel;

import java.util.List;

@MongoEntity(collection = "todos")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@Setter
public class UserWithTodosEntity extends PanacheMongoEntity {

    private @NotNull String id;
    private @NotNull List<@NotNull TodoEntity> todos;

    public static UserWithTodosEntity from(UserWithTodosModel model) {
        return new UserWithTodosEntity(
                model.username(),
                model.todos().stream().map(TodoEntity::from).toList()
        );
    }

    public static UserWithTodosModel to(UserWithTodosEntity entity) {
        return new UserWithTodosModel(
                entity.getId(),
                entity.getTodos().stream().map(TodoEntity::to).toList()
        );
    }
}