package ru.practicum.users;

import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(UserDto user);

    void deleteUser(Long userId);
}
