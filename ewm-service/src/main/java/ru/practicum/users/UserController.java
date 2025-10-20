package ru.practicum.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0", required = false) Integer from,
                                     @RequestParam(defaultValue = "10", required = false) Integer size) {
        return userService.getAllUsers(ids, from, size);
    }

    @ResponseStatus(CREATED)
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }


    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }
}
