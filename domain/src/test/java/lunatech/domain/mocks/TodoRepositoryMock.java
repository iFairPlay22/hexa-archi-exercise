package lunatech.domain.mocks;

import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.TodoRepositoryPort;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoRepositoryMock implements TodoRepositoryPort {
    private final @NotNull List<@NotNull UserWithTodosModel> userWithTodosEntities;

    public TodoRepositoryMock(@NotNull UserWithTodosModel... userWithTodosEntities) {
        this.userWithTodosEntities = new ArrayList<>(List.of(userWithTodosEntities));
    }

    @Override
    public Optional<UserWithTodosModel> findByUserName(String username) {
        return userWithTodosEntities.stream().filter(u -> u.username().equals(username)).findFirst();
    }

    @Override
    public Optional<TodoModel> findByUserNameAndId(String username, ObjectId todoId) {
        return findByUserName(username).flatMap(u -> u.todos().stream().filter(t -> t.id().equals(todoId)).findFirst());
    }

    @Override
    public void save(UserWithTodosModel user) {
        userWithTodosEntities.removeIf(u -> u.username().equals(user.username()));
        userWithTodosEntities.add(user);
    }
}
