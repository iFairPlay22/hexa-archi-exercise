package lunatech.domain.todos.models;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lunatech.test.resources.fixtures.AFixture;
import org.bson.types.ObjectId;

import java.util.List;

public class TodoModelFixtures extends AFixture<TodoModel, TodoModelFixtures.Params> {

    @Override
    public @NotNull TodoModelFixtures.Params defaultParams() {
        return TodoModelFixtures.Params.builder().build();
    }

    @Override
    public @NotNull TodoModel one(@NotNull Params params) {
        return new TodoModel(
                params.id != null ? params.id : new ObjectId(),
                params.title != null ? params.title : faker.job().title(),
                params.tags != null ? params.tags : List.of(faker.job().field()),
                params.done != null ? params.done : faker.random().nextBoolean()
        );
    }

    @Builder
    public static class Params {
        private final @NotNull ObjectId id;
        private final @NotNull String title;
        private final @NotNull List<@NotNull String> tags;
        private final @NotNull Boolean done;
    }

    public static class Samples {

        public static final TodoModelFixtures TODO_MODEL_FIXTURES = new TodoModelFixtures();

    }
}
