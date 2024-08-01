package lunatech.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
public final class InvalidTodoFormatException extends AppException {
    final List<String> violations;
}
