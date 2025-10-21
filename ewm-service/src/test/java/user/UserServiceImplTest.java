package user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.users.User;
import ru.practicum.users.UserMapper;
import ru.practicum.users.UserRepository;
import ru.practicum.users.UserServiceImpl;
import ru.practicum.users.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ru.practicum.EwmServiceApp.class)
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceImpl userService;
    private UserDto userDto1;
    private User user1;
    private User user2;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setName("Test User1");

        user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setName("Test User2");

        userDto1 = new UserDto();
        userDto1.setEmail("user1@mail.com");
        userDto1.setName("Test User1");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllUsersFromSize() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<UserDto> users = userService.getAllUsers(Collections.emptyList(), 0, 10);

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Test User1")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Test User2")));
    }

    @Test
    void getAllUsersWithIds() {
        User savedUser1 = userRepository.save(user1);
        userRepository.save(user2);
        List<Long> ids = Collections.singletonList(savedUser1.getId());

        List<UserDto> users = userService.getAllUsers(ids, 0, 10);

        assertEquals(1, users.size());
        assertEquals("Test User1", users.get(0).getName());
        assertEquals("user1@mail.com", users.get(0).getEmail());
    }

    @Test
    void createUser() {
        UserDto createdUser = userService.createUser(userDto1);

        assertNotNull(createdUser.getId());
        assertEquals(userDto1.getName(), createdUser.getName());
        assertEquals(userDto1.getEmail(), createdUser.getEmail());
        assertTrue(userRepository.existsById(createdUser.getId()));
    }

    @Test
    void createUserDuplicateName() {
        userRepository.save(user1);

        CommonConflictException exception = assertThrows(
                CommonConflictException.class,
                () -> userService.createUser(userDto1)
        );
        assertEquals("User with name " + userDto1.getName() + " already exists.", exception.getMessage());
    }

    @Test
    void deleteUser() {
        User savedUser = userRepository.save(user1);

        userService.deleteUser(savedUser.getId());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void deleteUserUserNotFound() {
        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> userService.deleteUser(999L)
        );
        assertEquals("User with id 999 was not found.", exception.getMessage());
    }
}