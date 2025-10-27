package comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.comments.Comment;
import ru.practicum.comments.CommentMapper;
import ru.practicum.comments.CommentRepository;
import ru.practicum.comments.CommentServiceImpl;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Comment> criteriaQuery;

    @Mock
    private Root<Comment> commentRoot;

    @Mock
    private TypedQuery<Comment> typedQuery;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Event event;
    private Comment comment;
    private CommentDto commentDto;
    private NewCommentDto newCommentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Test Comment");

        newCommentDto = new NewCommentDto();
        newCommentDto.setContent("New Comment");
    }


    @Test
    void getCommentById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.getCommentById(1L, 1L);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void getCommentByIdUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.getCommentById(1L, 1L)
        );

        assertEquals("User with id: 1 was not found ", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void getCommentByIdCommentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.getCommentById(1L, 1L)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentByIdNotCommentCreator() {
        User otherUser = new User();
        otherUser.setId(2L);
        comment.setUser(otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommonConflictException exception = assertThrows(
                CommonConflictException.class,
                () -> commentService.getCommentById(1L, 1L)
        );

        assertEquals("User should be comment author", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void deleteCommentByUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteCommentByUserId(1L, 1L);

        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteCommentByUserIdUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.deleteCommentByUserId(1L, 1L)
        );

        assertEquals("User with id: 1 was not found ", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void deleteCommentByUserIdCommentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.deleteCommentByUserId(1L, 1L)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void deleteCommentByUserIdNotCommentCreator() {
        User otherUser = new User();
        otherUser.setId(2L);
        comment.setUser(otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommonConflictException exception = assertThrows(
                CommonConflictException.class,
                () -> commentService.deleteCommentByUserId(1L, 1L)
        );

        assertEquals("User should be comment author", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentsByUserAndParamsInvalidTimeRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        CommonConflictException exception = assertThrows(
                CommonConflictException.class,
                () -> commentService.getCommentsByUserAndParams(1L, start, end, 0, 10)
        );

        assertEquals("Wrong date parameters", exception.getMessage());
        verify(entityManager, never()).getCriteriaBuilder();
    }

    @Test
    void updateCommentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateCommentUser(1L, 1L, newCommentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        assertEquals(newCommentDto.getContent(), comment.getContent());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(commentRepository).save(comment);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void updateCommentUserUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.updateCommentUser(1L, 1L, newCommentDto)
        );

        assertEquals("User with id: 1 was not found ", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void updateCommentUserCommentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.updateCommentUser(1L, 1L, newCommentDto)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void updateCommentUserNotCommentCreator() {
        User otherUser = new User();
        otherUser.setId(2L);
        comment.setUser(otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommonConflictException exception = assertThrows(
                CommonConflictException.class,
                () -> commentService.updateCommentUser(1L, 1L, newCommentDto)
        );

        assertEquals("User should be comment author", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(commentRepository).findById(1L);
    }

    @Test
    void addComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.addComment(1L, 1L, newCommentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(userRepository).findById(1L);
        verify(eventsRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void addCommentUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.addComment(1L, 1L, newCommentDto)
        );

        assertEquals("User with id 1 not found.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(eventsRepository, never()).findById(anyLong());
    }

    @Test
    void addCommentEventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.addComment(1L, 1L, newCommentDto)
        );

        assertEquals("Event with id 1 not found.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(eventsRepository).findById(1L);
    }

    /* ADMIN METHODS */

    @Test
    void deleteCommentByIdAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteCommentByIdAdmin(1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteCommentByIdAdminCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.deleteCommentByIdAdmin(1L)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void getCommentByIdAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.getCommentByIdAdmin(1L);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentByIdAdminCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.getCommentByIdAdmin(1L)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentsByEventIdAdmin() {
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentDtos = List.of(commentDto);

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findAllByEventId(eq(1L), any(PageRequest.class))).thenReturn(comments);
        when(commentMapper.toCommentDtos(comments)).thenReturn(commentDtos);

        List<CommentDto> result = commentService.getCommentsByEventIdAdmin(1L, 0, 10);

        assertNotNull(result);
        assertEquals(commentDtos, result);
        verify(eventsRepository).findById(1L);
        verify(commentRepository).findAllByEventId(eq(1L), any(PageRequest.class));
    }

    @Test
    void getCommentsByEventIdAdminEventNotFound() {
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.getCommentsByEventIdAdmin(1L, 0, 10)
        );

        assertEquals("Event with id: 1 was not found", exception.getMessage());
        verify(eventsRepository).findById(1L);
        verify(commentRepository, never()).findAllByEventId(anyLong(), any());
    }

    @Test
    void updateCommentByIdAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.updateCommentByIdAdmin(newCommentDto, 1L);

        assertNotNull(result);
        assertEquals(commentDto, result);
        assertEquals(newCommentDto.getContent(), comment.getContent());
        verify(commentRepository).findById(1L);
        verify(commentRepository).save(comment);
    }

    @Test
    void updateCommentByIdAdminCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CommonNotFoundException exception = assertThrows(
                CommonNotFoundException.class,
                () -> commentService.updateCommentByIdAdmin(newCommentDto, 1L)
        );

        assertEquals("Comment was 1 not found", exception.getMessage());
        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).save(any());
    }
}
