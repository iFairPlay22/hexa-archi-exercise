package lunatech.entities;

import java.util.List;
import jakarta.validation.constraints.*;
import lombok.*;
import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TodoEntity {

    /**
     * Unique identifier
     * @example "6671c2707167b70bb531bef2"
     */
    @NotNull(message = "Todo id should be set")
    public ObjectId todoId;

    /**
     * Title of the todo
     * @example "Brush my teeth"
     */
    @NotNull(message = "Title should be set")
    @Size(min = 3, max = 50, message="Title length should be between 3 and 30 characters")
    public String title;

    /**
     * List of tags
     * @example [ "healthcare", "work", "sport" ]
     */
    @NotNull(message = "Tags should be set")
    public List<
            @NotNull(message = "Tag should be set") String
            > tags;

    /**
     * True if the task is done, false otherwise
     * @example true
     */
    @NotNull(message = "Done should be set")
    public Boolean done;
}

