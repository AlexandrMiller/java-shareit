package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    public Item postItem(long ownerId, ItemDto itemDto);

    public Item updateItem(ItemDto itemDto,long itemId,long ownerId);

    public ItemDto getItemByIdForAnyUser(long itemId);

    public Collection<ItemDto> searchItems(String text);
}
