package lunatech.infra.auth.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lunatech.domain.auth.models.AuthModel;

@MongoEntity(collection = "auth")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class AuthEntity extends PanacheMongoEntity {
    private @NotNull String id;
    private @NotNull String password;
    private @NotNull String role;

    public static AuthEntity from(AuthModel entity) {
        return new AuthEntity(
                entity.username(),
                entity.password(),
                from(entity.role())
        );
    }

    public static AuthModel to(AuthEntity staticEntity) {
        return new AuthModel(
                staticEntity.getId(),
                staticEntity.getPassword(),
                from(staticEntity.getRole())
        );
    }

    private static AuthModel.Role from(String role) {
        return switch (role) {
            case AuthModel.Role.Names.ADMIN -> AuthModel.Role.ADMIN;
            case AuthModel.Role.Names.REGULAR -> AuthModel.Role.REGULAR;
            default -> AuthModel.Role.UNKNOWN;
        };
    }

    private static String from(AuthModel.Role role) {
        return switch (role) {
            case ADMIN -> AuthModel.Role.Names.ADMIN;
            case REGULAR -> AuthModel.Role.Names.REGULAR;
            case UNKNOWN -> AuthModel.Role.Names.UNKNOWN;
        };
    }

}
