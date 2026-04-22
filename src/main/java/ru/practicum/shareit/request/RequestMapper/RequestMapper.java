package ru.practicum.shareit.request.RequestMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForReqDto;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {


    private final ItemRepository itemRepository;


    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(null,itemRequestDto.getDescription(), LocalDateTime.now(), user);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(null,itemRequest.getDescription(),itemRequest.getCreated(), new ArrayList<>());
    }

    public ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findItemsByRequestId(itemRequest.getId());

        List<ItemForReqDto> itemDto = items.stream().map(ItemMapper::toItemForReqDto).toList();

        return new ItemRequestDto(itemRequest.getId(),itemRequest.getDescription(),itemRequest.getCreated(),itemDto);
    }
        }
