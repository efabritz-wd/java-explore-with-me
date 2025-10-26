package ru.practicum.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setContent(commentDto.getContent());
        comment.setCreated(commentDto.getCreated());

        if (commentDto.getUserName() != null) {
            User user = userRepository.findByName(commentDto.getUserName()).orElseThrow(() ->
                    new CommonNotFoundException("User with name: " + commentDto.getUserName() + " was not found."));
            comment.setUser(user);
        }

        if (commentDto.getEventId() != null) {
            Event event = eventsRepository.findById(commentDto.getEventId()).orElseThrow(() ->
                    new CommonNotFoundException("User with id: " + commentDto.getEventId() + " was not found."));
            comment.setEvent(event);
        }

        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreated(comment.getCreated());
        commentDto.setUserName(comment.getUser() != null ? comment.getUser().getName() : null);
        commentDto.setEventId(comment.getEvent() != null ? comment.getEvent().getId() : null);

        return commentDto;
    }

    public List<CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .toList();
    }
}
