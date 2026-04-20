package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue
    private int id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id",referencedColumnName = "id")
    private User requestor;

    private LocalDateTime created;
}
