package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(" select b from Booking as b " +
            "where b.item.id = ?1 and b.start > ?2 order by b.start asc")
    List<Booking> findByItemIdOrderByStartAsc(Long itemId, Instant current);

    @Query(" select b from Booking as b " +
            "where b.item.id = ?1 and b.start < ?2 order by b.end desc")
    List<Booking> findByItemIdOrderByEndDesc(Long itemId, Instant current);

    List<Booking> findAllByItemId(Long itemId);

    @Query(value = "select b.* from bookings as b " +
            "where b.booker_id = ?1 and b.status = concat('', ?2, '') order by b.start_date desc", nativeQuery = true)
    List<Booking> findByBookerIdAndByStatus(Long userId, String status);

    @Query(" select b from Booking as b " +
            "where b.booker.id = ?1 order by b.start desc")
    List<Booking> findByBookerId(Long userId);

    @Query(value = "select b.* from bookings as b " +
            "left join items as it on b.item_id = it.id " +
            "where it.owner_id = ?1 and b.status = concat('', ?2, '') order by b.start_date desc", nativeQuery = true)
    List<Booking> findAllByOwner_IdByStatus(Long userId, String status);

    @Query(" select b from Booking b " +
            "where b.item.owner.id =  ?1 order by b.start desc")
    List<Booking> findAllByOwnerId(Long userId);
}
