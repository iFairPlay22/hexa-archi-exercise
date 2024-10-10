package lunatech.domain.todos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lunatech.domain.todos.models.TodoModel;
import lunatech.domain.todos.models.UserWithTodosModel;
import org.bson.types.ObjectId;

import java.util.Optional;

/**
 * Port that allows to store and retrieve users for _todo management purposes
 */
public interface TodoRepositoryPort {

    /**
     * Retrieves a _todo user entity given its username, without any side effects.
     *
     * @param username Username filter
     * @return The related todos user entity
     */
    @NotNull
    Optional<@NotNull UserWithTodosModel> findByUserName(@Valid @NotNull String username);

    /**
     * Retrieves a _todo entity given its username and _todo id, without any side effects.
     *
     * @param username Username filter
     * @param todoId   _Todo id filter
     * @return The related _todo entity
     */
    @NotNull
    Optional<@NotNull TodoModel> findByUserNameAndId(@Valid @NotNull String username, @Valid @NotNull ObjectId todoId);

    /**
     * Creates or updates a _todo user entity in the system
     *
     * @param user _Todo user to save
     */
    void save(@Valid @NotNull UserWithTodosModel user);

}
