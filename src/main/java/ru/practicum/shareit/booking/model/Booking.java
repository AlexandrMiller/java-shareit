package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Table(name = "bookings")
@Entity
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "start_date",nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date",nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id",referencedColumnName = "id",nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id",referencedColumnName = "id",nullable = false)
    private User booker;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public Booking(LocalDateTime start,LocalDateTime end,Item item,User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }
}
