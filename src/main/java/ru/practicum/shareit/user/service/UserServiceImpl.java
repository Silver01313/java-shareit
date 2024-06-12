package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {
       /* if (isUserAlreadyExist(user)) {
            log.debug("Пользователь с таким email уже существует");
            throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }*/
      return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(User user, Long userId) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

  /*      if (user.getEmail() != null && isEmailAlreadyExistsWhenUpdatesUser(user, userId)) {
                log.debug("Пользователь с таким email уже существует");
                throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }*/

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        newUser.setId(userId);
       return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getAll() {
        if (userRepository.findAll().isEmpty()) return new ArrayList<>();
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public User get(long userId) {
        if (!userRepository.findAll().contains(userRepository.findById(userId))) {
            log.debug("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        return userRepository.findById(userId);
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isUserAlreadyExist(User user) {
        return userRepository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }

    private boolean isEmailAlreadyExistsWhenUpdatesUser(User user, Long userId) {
        return userRepository.findAll().stream()
                .filter(v -> v.getId() != userId)
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }
}
