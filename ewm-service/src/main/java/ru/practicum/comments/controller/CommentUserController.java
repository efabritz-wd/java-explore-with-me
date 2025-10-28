package ru.practicum.comments.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.CommentService;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.utils.UtilPatterns;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentUserController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(CREATED)
    public CommentDto saveComment(@PathVariable Long eventId,
                                  @PathVariable Long userId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @PathVariable Long userId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.updateCommentUser(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getOwnCommentsByTimePeriode(@PathVariable Long userId,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN) LocalDateTime start,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = UtilPatterns.DATE_PATTERN) LocalDateTime end,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsByUserAndParams(userId, start, end, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId,
                                     @PathVariable Long userId) {
        return commentService.getCommentById(userId, commentId);
    }


    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long commentId,
                                    @PathVariable Long userId) {
        commentService.deleteCommentByUserId(userId, commentId);
    }
}