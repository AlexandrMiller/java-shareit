package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForReqDto;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    private final ItemRepository itemRepository;

    public ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findItemsByRequestId(itemRequest.getId());

        List<ItemForReqDto> itemDto = items.stream().map(ItemMapper::toItemForReqDto).toList();

        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), itemDto);
    }
}
