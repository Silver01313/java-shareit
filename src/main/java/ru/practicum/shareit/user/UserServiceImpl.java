package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(User user) {
      return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(User user, Long userId) {
       return UserMapper.toUserDto(userStorage.update(user,userId));
    }

    @Override
    public List<UserDto> getAll() {
        if (userStorage.getAll().isEmpty()) return new ArrayList<>();
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto get(long userId) {
        return UserMapper.toUserDto(userStorage.get(userId));
    }

    @Override
    public void remove(long userId) {
        userStorage.remove(userId);
    }
}
