package ru.practicum.comments;

import jakarta.validation.Valid;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto getCommentById(Long userId, Long commentId);

    void deleteCommentByUserId(Long userId, Long commentId);

    List<CommentDto> getCommentsByUserAndParams(Long userId, LocalDateTime start, LocalDateTime end, Integer from, Integer size);

    CommentDto updateCommentUser(Long userId, Long commentId, @Valid NewCommentDto newCommentDto);

    CommentDto addComment(Long userId, Long eventId, @Valid NewCommentDto newCommentDto);

    void deleteCommentByIdAdmin(Long commentId);

    CommentDto getCommentByIdAdmin(Long commentId);

    List<CommentDto> getCommentsByEventIdAdmin(Long eventId, Integer from, Integer size);

    CommentDto updateCommentByIdAdmin(@Valid NewCommentDto newCommentDto, Long commentId);
}
