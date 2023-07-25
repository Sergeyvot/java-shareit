package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindAllByItemId() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User author = userRepository.save(makeUser("Author", "author@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Крестовая отвертка", true, owner));

        Comment comment = makeComment("Все понравилось", newItem, author,
                Instant.now().minusSeconds(1000));
        repository.save(comment);


        TypedQuery<Comment> query = em.getEntityManager()
                .createQuery("Select c from Comment c where c.item.id = :itemId", Comment.class);
        List<Comment> result = query.setParameter("itemId", newItem.getId()).getResultList();

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getText(), equalTo(comment.getText()));
        assertThat(result.get(0).getItem().getId(), equalTo(comment.getItem().getId()));
        assertThat(result.get(0).getCreated(), notNullValue());
    }

    private User makeUser(String email, String name) {

        return User.builder()
                .name(name)
                .email(email).build();
    }

    private Item makeItem(String name, String description, Boolean available, User owner) {

        return Item.builder()
                .name(name)
                .description(description)
                .available(available)
                .owner(owner).build();
    }

    private Comment makeComment(String text, Item item, User author, Instant created) {

        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(created).build();
    }
}
