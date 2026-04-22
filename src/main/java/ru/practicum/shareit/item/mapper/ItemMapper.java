package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingTinyDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForReqDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getName(), item.getDescription(), item.getAvailable(),item.getRequestId());
    }

    public static Item toItem(ItemDto itemDto,User owner) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }

    public static ItemLastNextDto toItemLastNextDto(Item item,
                                                    BookingTinyDto lastBooking,
                                                    BookingTinyDto nextBooking,
                                                    List<CommentDto> comments) {
        return new ItemLastNextDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments);
    }

    public static ItemForReqDto toItemForReqDto(Item item) {
        return new ItemForReqDto(item.getId(),item.getName(),item.getOwner().getId());
    }
}
