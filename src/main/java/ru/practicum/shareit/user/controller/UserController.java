package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
       return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody User user, @PathVariable Long userId) {
        return userService.update(user,userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId){
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        userService.remove(userId);
    }

}
