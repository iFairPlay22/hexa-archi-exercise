package lunatech.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lunatech.security.Role;

import java.util.List;

@MongoEntity(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserEntity extends PanacheMongoEntity {

    /**
     * Username used for HTTP auth
     * Warning: it should be unique in the whole collection
     * @example "Nicolas"
     */
    @NotNull(message = "Username should be set")
    @Size(min = 3, max = 30, message="Username length should be between 3 and 30 characters")
    public String username;

    /**
     * Password used for HTTP auth
     * @example "1234"
     */
    @NotNull(message = "Password should be set")
    @Size(min = 3, max = 10, message="Password length should be between 3 and 30 characters")
    public String password;

    /**
     * Role of the user
     * @example "admin"
     */
    @NotNull(message = "Role should be set")
    @Size(min = 3, max = 10, message="Role length should be between 3 and 10 characters")
    public String role;

    /**
     * Associated todos
     * @example List.empty<TodoEntity>
     */
    @NotNull(message = "Todos should be set")
    public List<@NotNull(message = "Todos.* should not be null") TodoEntity> todos;
}