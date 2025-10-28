package ru.practicum.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comments.dto.CommentDto;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

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
