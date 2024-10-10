package lunatech.infra.todos;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.TodoRepositoryPort;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import lunatech.infra.todos.entities.TodoEntity;
import lunatech.infra.todos.entities.UserWithTodosEntity;
import org.bson.types.ObjectId;

import java.util.Optional;

public class TodoRepositoryAdapter implements TodoRepositoryPort, PanacheMongoRepository<UserWithTodosEntity> {

    @Override
    public @NotNull Optional<@NotNull UserWithTodosModel> findByUserName(@Valid @NotNull String username) {
        return stream("_id", username)
                .map(UserWithTodosEntity::to)
                .findFirst();
    }

    @Override
    public @NotNull Optional<@NotNull TodoModel> findByUserNameAndId(@Valid @NotNull String username, @Valid @NotNull ObjectId todoId) {
        return stream("_id = ?1, todos._id = ?2", username, todoId)
                .flatMap(usersWithTodos -> usersWithTodos.getTodos().stream())
                .filter(t -> t.getId().equals(todoId))
                .map(TodoEntity::to)
                .findFirst();
    }

    @Override
    public void save(@Valid @NotNull UserWithTodosModel user) {
        persistOrUpdate(UserWithTodosEntity.from(user));
    }
}
