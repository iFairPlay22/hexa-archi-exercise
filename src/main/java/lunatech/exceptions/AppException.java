package lunatech.exceptions;

public sealed abstract class AppException extends Exception
        permits InvalidTodoFormatException, TodoAlreadyExistingException, TodoNotFoundException, UnauthorizedActionException {}

