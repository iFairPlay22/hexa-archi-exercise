package lunatech.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;
import java.util.Map;

@MongoEntity(collection = "commands")
public class CommandsEntity extends PanacheMongoEntity {
    public ObjectId id;
    public ZonedDateTime ts;
    public Map<String, Integer> basket;
    public Integer totalPrice;

    public CommandsEntity() {}

    public CommandsEntity(
        @NotNull ObjectId id,
        @NotNull ZonedDateTime ts,
        @NotNull Map<String, Integer> basket,
        @NotNull Integer totalPrice
    ) {
        this.id = id;
        this.ts = ts;
        this.basket = basket;
        this.totalPrice = totalPrice;
    }
}