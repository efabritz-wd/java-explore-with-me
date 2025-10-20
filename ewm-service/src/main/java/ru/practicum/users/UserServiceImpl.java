package ru.practicum.users;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.users.dto.UserDto;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null || ids.isEmpty()) {
            Pageable pageable = PageRequest.of(from / size, size);

            List<User> users = userRepository.findAll(pageable).getContent();

            return userMapper.toUserDtos(users);
        }
        return userMapper.toUserDtos(userRepository.findAllById(ids));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            throw new CommonConflictException("User with name " + userDto.getName() + " already exists.");
        }
        User user = userMapper.toUser(userDto);
        User userSaved = userRepository.save(user);
        return userMapper.toUserDto(userSaved);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CommonNotFoundException("User with id " + userId + " was not found.");
        }
        userRepository.deleteById(userId);
    }
}
