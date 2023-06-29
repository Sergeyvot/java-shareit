package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dao.UserRepository;

@Component
public class CommentMapper {

    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Autowired
    public CommentMapper(ItemRepository repository, UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Comment toComment(Long authorId, Long itemId, CommentDto commentDto) {
        Comment.CommentBuilder comment = Comment.builder();

        if ( commentDto.getId() != null ) {
            comment.id( commentDto.getId() );
        }
        comment.text(commentDto.getText());
        comment.item(repository.findById(itemId).get());
        comment.author(userRepository.findById(authorId).get());

        return comment.build();
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto.CommentDtoBuilder commentDto = CommentDto.builder();

        commentDto.id(comment.getId());
        commentDto.text(comment.getText());
        commentDto.authorName(comment.getAuthor().getName());
        commentDto.created(comment.getCreated());

        return commentDto.build();
    }
}
