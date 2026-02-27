package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class Item {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Boolean available;

    private long owner;

    public Item(String name, String description, Boolean available, long owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

}
