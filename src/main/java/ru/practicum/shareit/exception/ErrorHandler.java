package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.storage.ItemStorageImpl;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.Map;

@RestControllerAdvice(assignableTypes = { UserStorageImpl.class, UserService.class, UserController.class,
        UserServiceImpl.class, UserRepository.class,
        ItemStorageImpl.class, ItemStorageImpl.class, ItemController.class,})
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAlreadyExists(AlreadyExistsException e) {
        return Map.of("Объект уже существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoArguments(NoArgumentsException e) {
        return Map.of("Не корректный запрос", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNoAcces(NoAccessException e) {
        return Map.of("У вас нет доступа", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NotFoundException e ) {
        return Map.of("Объект не найден", e.getMessage());
    }
}
