package ru.practicum.comments;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import static org.springframework.data.domain.PageRequest.of;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final EntityManager entityManager;

    /* USER */

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CommonNotFoundException("User with id: " + userId + " was not found ");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        checkIfUserIsCommentCreator(comment, userId);

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteCommentByUserId(Long userId, Long commentId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CommonNotFoundException("User with id: " + userId + " was not found ");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        checkIfUserIsCommentCreator(comment, userId);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByUserAndParams(Long userId, LocalDateTime start, LocalDateTime end, Integer from, Integer size) {

        validateTimeRange(start, end);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
        Root<Comment> commentRoot = criteriaQuery.from(Comment.class);

        Predicate conditions = buildQueryConditions(criteriaBuilder, commentRoot, userId, start, end);

        criteriaQuery
                .select(commentRoot)
                .where(conditions)
                .orderBy(criteriaBuilder.asc(commentRoot.get("created")));

        TypedQuery<Comment> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size);

        List<Comment> results = typedQuery.getResultList();
        return commentMapper.toCommentDtos(results);
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new CommonConflictException("Wrong date parameters");
        }
    }

    private Predicate buildQueryConditions(CriteriaBuilder criteriaBuilder,
                                           Root<Comment> root,
                                           Long userId,
                                           LocalDateTime start,
                                           LocalDateTime end) {
        Predicate condition = criteriaBuilder.equal(root.get("user"), userId);

        if (start != null) {
            condition = criteriaBuilder.and(condition,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("created").as(LocalDateTime.class), start));
        }

        if (end != null) {
            condition = criteriaBuilder.and(condition,
                    criteriaBuilder.lessThanOrEqualTo(root.get("created").as(LocalDateTime.class), end));
        }

        return condition;
    }

    @Override
    public CommentDto updateCommentUser(Long userId, Long commentId, NewCommentDto newCommentDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CommonNotFoundException("User with id: " + userId + " was not found ");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        checkIfUserIsCommentCreator(comment, userId);

        comment.setContent(newCommentDto.getContent());
        Comment newComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(newComment);
    }

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new CommonNotFoundException("User with id " + userId + " not found."));

        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new CommonNotFoundException("Event with id " + eventId + " not found."));
        Comment comment = new Comment();
        comment.setContent(newCommentDto.getContent());
        comment.setCreated(LocalDateTime.now());
        comment.setUser(user);
        comment.setEvent(event);
        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.toCommentDto(commentSaved);
    }

    /*  ADMIN */

    @Override
    public void deleteCommentByIdAdmin(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto getCommentByIdAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEventIdAdmin(Long eventId, Integer from, Integer size) {
        eventsRepository.findById(eventId).orElseThrow(
                () -> new CommonNotFoundException("Event with id: " + eventId + " was not found"));

        List<Comment> comments = commentRepository.findAllByEventId(eventId, of(from / size, size));

        return commentMapper.toCommentDtos(comments);
    }

    @Override
    public CommentDto updateCommentByIdAdmin(NewCommentDto newCommentDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommonNotFoundException("Comment was " + commentId + " not found"));

        comment.setContent(newCommentDto.getContent());
        Comment newComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(newComment);
    }

    public void checkIfUserIsCommentCreator(Comment comment, Long userId) {
        Long commentCreatorId = comment.getUser().getId();

        if (!userId.equals(commentCreatorId)) {
            throw new CommonConflictException("User should be comment author");
        }
    }
}
