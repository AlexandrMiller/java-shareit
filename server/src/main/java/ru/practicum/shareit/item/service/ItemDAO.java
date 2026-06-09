package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingTinyDto;
import ru.practicum.shareit.booking.jpaBooking.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingsService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.jpaItem.CommentRepository;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.service.UserDao;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemDAO implements ItemInterface {

    private final UserDao userDao;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingsService bookingsService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDAO(UserDao userDao, ItemRepository itemRepository, UserRepository userRepository, BookingsService bookingsService, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userDao = userDao;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingsService = bookingsService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }


    @Transactional
    public Item postItem(long userId, ItemDto itemDto) {
        log.info("Запрос на создание вещи");
        validateItemDto(itemDto);
        User user = userDao.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto,user);
        if (itemDto.getRequestId() == null) {
            item.setRequestId(null);
        } else {
            item.setRequestId(itemDto.getRequestId());
        }
        Item savedItem = itemRepository.save(item);
        log.info("Запрос на создание вещи выполнен");

        return savedItem;
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

    @Transactional
    public Item updateItem(ItemDto itemDto, long itemId, long ownerId) {
        log.info("Запрос на обновление данных вещи");
        Item itemToUpdate = itemRepository
                .findById(itemId).orElseThrow(() -> new NotFoundException("предмета не существует"));


        if (itemToUpdate.getOwner().getId() != ownerId) {
            throw new NotFoundException("У данного пользовталея нет такой вещи");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemToUpdate.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
             itemToUpdate.setDescription(itemDto.getDescription().trim());
        }

        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(itemToUpdate);

        log.info("Запрос выполнен");


        return updatedItem;

    }

    @Transactional(readOnly = true)
    public ItemLastNextDto getItemById(long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        List<CommentDto> commentDto = comments.stream().map(CommentMapper::toCommentDto).toList();

        BookingTinyDto lastBooking = null;

        BookingTinyDto nextBooking = null;

        if (Objects.equals(item.getOwner().getId(),userId)) {
            lastBooking = bookingsService.getLastBooking(item.getId());

            nextBooking = bookingsService.getNextBooking(item.getId());
        }

        return ItemMapper.toItemLastNextDto(item,lastBooking,nextBooking,commentDto);
    }


    @Transactional(readOnly = true)
    public List<ItemLastNextDto> getItemsByOwner(long id) {

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("user not found");
        }

        List<Item> items = itemRepository.getItemsByOwnerId(id);

        return items.stream()
                .map(item -> {
                    BookingTinyDto lastBooking = bookingsService.getLastBooking(item.getId());
                    BookingTinyDto nextBooking = bookingsService.getNextBooking(item.getId());
                    List<CommentDto> comments = commentRepository
                            .findByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    return ItemMapper.toItemLastNextDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<ItemDto> searchItem(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();

        List<Item> items = itemRepository.searchItems(searchText);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto,Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не существует"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));

        boolean canComment = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(userId,itemId, Status.APPROVED, LocalDateTime.now());

        if (!canComment) {
            throw new ValidationException("Вы не бронировали вещь или бронь еще не закончилась.Отзыв не доступен");
        }

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        Comment comment = CommentMapper.toComment(commentDto,item,user);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }
}
