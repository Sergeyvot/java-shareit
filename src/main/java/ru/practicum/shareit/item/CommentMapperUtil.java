package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public final class CommentMapperUtil {

    private CommentMapperUtil() {
        
    }

    public static Comment toComment(User author, Item item, CommentDto commentDto) {
        Comment.CommentBuilder comment = Comment.builder();

        if (commentDto.getId() != null) {
            comment.id(commentDto.getId());
        }
        comment.text(commentDto.getText());
        comment.item(item);
        comment.author(author);

        return comment.build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto.CommentDtoBuilder commentDto = CommentDto.builder();

        commentDto.id(comment.getId());
        commentDto.text(comment.getText());
        commentDto.authorName(comment.getAuthor().getName());
        commentDto.created(LocalDateTime.ofInstant(comment.getCreated(), ZoneId.systemDefault()));

        return commentDto.build();
    }
}
