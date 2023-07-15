package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindAllByItemId() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        Item newItem1 = repository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Item newItem2 = repository.save(makeItem("Дрель", "Почти отвертка", true, owner));
        String text = "оТверТ";

        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("Select i from Item i where upper(i.name) like upper(concat('%', :text, '%')) " +
                        "or upper(i.description) like upper(concat('%', :text, '%'))", Item.class);
        List<Item> result = query.setParameter("text", text).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getName(), equalTo(newItem1.getName()));
        assertThat(result.get(0).getDescription(), equalTo(newItem1.getDescription()));
        assertThat(result.get(0).getOwner().getId(), equalTo(owner.getId()));
        assertThat(result.get(0).getRequest(), nullValue());
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getName(), equalTo(newItem2.getName()));
        assertThat(result.get(1).getDescription(), equalTo(newItem2.getDescription()));
        assertThat(result.get(1).getOwner().getId(), equalTo(owner.getId()));
        assertThat(result.get(1).getRequest(), nullValue());
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
}
