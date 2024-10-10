package lunatech.test.resources.fixtures;

import com.github.javafaker.Faker;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Fixture abstract class to generate objects with random values
 *
 * @param <R> Generated object type
 * @param <P> Parameters object type (used for the generation)
 */
public abstract class AFixture<R, P> {

    protected Faker faker = new Faker();

    /**
     * Generates default param values, used when called the one() function without parameters
     *
     * @return Default params values
     */
    public abstract @NotNull P defaultParams();

    /**
     * Generates an object with the default param values
     *
     * @return The generated object
     */
    public @NotNull R one() {
        return one(defaultParams());
    }

    /**
     * Generates an object with the given param values
     *
     * @return The generated object
     */
    public abstract @NotNull R one(@NotNull P params);


    /**
     * Generates a list objects with the default param values
     *
     * @param length The number of objects to generate
     * @return A list containing the generated objects
     */
    public @NotNull List<@NotNull R> many(@Positive int length) {
        return many(length, defaultParams());
    }

    /**
     * Generates a list objects with the given param values
     *
     * @param length The number of objects to generate
     * @param params Params arguments
     * @return A list containing the generated objects
     */
    public @NotNull List<@NotNull R> many(@Positive int length, P params) {
        return IntStream.range(0, length)
                .mapToObj(i -> one(params))
                .toList();
    }

}
