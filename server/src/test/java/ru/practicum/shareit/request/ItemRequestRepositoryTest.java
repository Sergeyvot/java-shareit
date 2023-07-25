package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindAllOrderByCreatedDesc() {
        User requester = userRepository.save(makeUser("Requester", "Requester@mail.ru"));
        ItemRequest request1 = repository.save(makeItemRequest("Нужна отвертка", requester,
                Instant.now().minusSeconds(1000)));
        ItemRequest request2 = repository.save(makeItemRequest("Нужен чайник", requester,
                Instant.now().minusSeconds(500)));

        TypedQuery<ItemRequest> query = em.getEntityManager()
                .createQuery("Select ir from ItemRequest as ir order by ir.created desc", ItemRequest.class);
        List<ItemRequest> result = query.getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getDescription(), equalTo(request2.getDescription()));
        assertThat(result.get(0).getRequestor().getId(), equalTo(requester.getId()));
        assertThat(result.get(0).getCreated(), equalTo(request2.getCreated()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getDescription(), equalTo(request1.getDescription()));
        assertThat(result.get(1).getRequestor().getId(), equalTo(requester.getId()));
        assertThat(result.get(1).getCreated(), equalTo(request1.getCreated()));
    }

    private User makeUser(String email, String name) {

        return User.builder()
                .name(name)
                .email(email).build();
    }

    private ItemRequest makeItemRequest(String description, User requestor, Instant created) {

        return ItemRequest.builder()
                .description(description)
                .requestor(requestor)
                .created(created).build();
    }
}
