package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemInterface {

    Item postItem(long userId, ItemDto itemDto);

    Item updateItem(ItemDto itemDto, long itemId, long ownerId);

    ItemLastNextDto getItemById(long id, Long userId);

    List<ItemLastNextDto> getItemsByOwner(long id);

    Collection<ItemDto> searchItem(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
