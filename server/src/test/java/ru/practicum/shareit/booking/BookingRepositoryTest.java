package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindByItemIdOrderByStartAsc() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker1 = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        User booker2 = userRepository.save(makeUser("Booker2", "Booker2@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().plusSeconds(1500),
                Instant.now().plusSeconds(2000), newItem, booker1, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().plusSeconds(500),
                Instant.now().plusSeconds(1000), newItem, booker2, Status.WAITING));

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.item.id = :itemId and b.start > :current" +
                        " order by b.start asc", Booking.class);
        List<Booking> result = query.setParameter("itemId", newItem.getId())
                .setParameter("current", Instant.now()).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker2.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker1.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void testFindByItemIdOrderByEndDesc() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker1 = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        User booker2 = userRepository.save(makeUser("Booker2", "Booker2@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().minusSeconds(1000),
                Instant.now().minusSeconds(500), newItem, booker1, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().minusSeconds(2500),
                Instant.now().minusSeconds(1000), newItem, booker2, Status.WAITING));

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.item.id = :itemId " +
                        "and b.start < :current order by b.end desc", Booking.class);
        List<Booking> result = query.setParameter("itemId", newItem.getId())
                .setParameter("current", Instant.now()).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker1.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking1.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker2.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking2.getStatus()));
    }

    @Test
    void testFindByBookerIdAndByStatus() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().plusSeconds(500),
                Instant.now().plusSeconds(1000), newItem, booker, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().plusSeconds(1500),
                Instant.now().plusSeconds(2000), newItem, booker, Status.WAITING));
        String status = "WAITING";

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.booker.id = :bookerId " +
                        "and b.status = concat('', :status, '') order by b.start desc", Booking.class);
        List<Booking> result = query.setParameter("bookerId", booker.getId())
                .setParameter("status", status).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void testFindByBookerId() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().plusSeconds(500),
                Instant.now().plusSeconds(1000), newItem, booker, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().plusSeconds(1500),
                Instant.now().plusSeconds(2000), newItem, booker, Status.WAITING));

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.booker.id = :bookerId " +
                        "order by b.start desc", Booking.class);
        List<Booking> result = query.setParameter("bookerId", booker.getId()).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void testFindAllByOwner_IdByStatus() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().plusSeconds(500),
                Instant.now().plusSeconds(1000), newItem, booker, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().plusSeconds(1500),
                Instant.now().plusSeconds(2000), newItem, booker, Status.WAITING));
        String status = "WAITING";

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.item.owner.id = :ownerId " +
                        "and b.status = concat('', :status, '') order by b.start desc", Booking.class);
        List<Booking> result = query.setParameter("ownerId", owner.getId())
                .setParameter("status", status).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void testFindAllByOwnerId() {
        User owner = userRepository.save(makeUser("Owner", "owner@mail.ru"));
        User booker = userRepository.save(makeUser("Booker1", "Booker1@mail.ru"));
        Item newItem = itemRepository.save(makeItem("Отвертка", "Почти новая", true, owner));
        Booking booking1 = repository.save(makeBooking(Instant.now().plusSeconds(500),
                Instant.now().plusSeconds(1000), newItem, booker, Status.WAITING));
        Booking booking2 = repository.save(makeBooking(Instant.now().plusSeconds(1500),
                Instant.now().plusSeconds(2000), newItem, booker, Status.WAITING));

        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("select b from Booking as b where b.item.owner.id = :ownerId " +
                        "order by b.start desc", Booking.class);
        List<Booking> result = query.setParameter("ownerId", owner.getId()).getResultList();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getStart(), equalTo(booking2.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.get(0).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(result.get(1).getId(), notNullValue());
        assertThat(result.get(1).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(1).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(1).getItem().getId(), equalTo(newItem.getId()));
        assertThat(result.get(1).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(1).getStatus(), equalTo(booking1.getStatus()));
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

    private Booking makeBooking(Instant start, Instant end, Item item, User booker, Status status) {

        return Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status).build();
    }
}
