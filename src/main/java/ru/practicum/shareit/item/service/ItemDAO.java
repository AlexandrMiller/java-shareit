package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.userDao;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemDAO {


    private final userDao userDao;

    Map<Long,Item> items = new HashMap<>();

    private long itemId = 0;

    public ItemDAO(userDao userDao) {
        this.userDao = userDao;
    }


    public Item postItem(long userId, ItemDto itemDto) {
        log.info("Запрос на создание вещи");
        validateItemDto(itemDto);
        User user = userDao.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto,userId);
        item.setOwner(user.getId());
        item.setId(generateId());
        items.put(item.getId(),item);
        log.info("Запрос на создание вещи выполнен");

        return item;
    }

    private void validateItemDto(ItemDto dto) {
        if (dto == null) {
            throw new ValidationException("ItemDTO не может быть null");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }

    public long generateId() {
        return ++itemId;
    }

    public Item updateItem(ItemDto itemDto, long itemId, long ownerId) {
        log.info("Запрос на обновление данных вещи");
        Item itemToUpdate = getItemById(itemId);


        if (itemToUpdate.getOwner() != ownerId) {
            throw new NotFoundException("У данного пользовталея нет такой вещи");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemToUpdate.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
             itemToUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId,itemToUpdate);

        log.info("Запрос выполнен");


        return itemToUpdate;

    }

    public Item getItemById(long id) {

        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь не найдена");
        }

        return items.get(id);
    }

    public ItemDto getItemDto(long id) {
        return ItemMapper.toItemDto(getItemById(id));
    }

    public List<ItemDto> getItemsByOwner(long id) {
        return items.values().stream().filter(item -> Objects.equals(item.getOwner(),id))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> searchItem(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


}
