package ru.practicum.users;

import org.springframework.stereotype.Component;
import ru.practicum.users.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toUser(UserDto userModelDto) {
        if (userModelDto == null) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id(userModelDto.getId());
        user.name(userModelDto.getName());
        user.email(userModelDto.getEmail());

        return user.build();
    }

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id(user.getId());
        userDto.name(user.getName());
        userDto.email(user.getEmail());

        return userDto.build();
    }

    public List<UserDto> toUserDtos(List<User> usersList) {
        if (usersList == null) {
            return null;
        }

        List<UserDto> list = new ArrayList<>(usersList.size());
        for (User user : usersList) {
            list.add(toUserDto(user));
        }

        return list;
    }
}
