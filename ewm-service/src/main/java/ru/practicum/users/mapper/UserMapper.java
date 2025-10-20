package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.User;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    List<UserDto> toUserDtos(List<User> users);

    User toUser(UserDto userDto);

    List<User> toUsersFromDto(List<UserDto> userDtos);

    UserShortDto toUserShortDto(User user);

    List<UserShortDto> toUserShortDtos(List<User> users);

    User toUser(UserShortDto userShortDto);

    List<User> toUsersFromShotDto(List<UserShortDto> userShortDtos);
}
