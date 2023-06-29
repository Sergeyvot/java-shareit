package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date")
    Instant start;
    @Column(name = "end_date")
    Instant end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;
}
