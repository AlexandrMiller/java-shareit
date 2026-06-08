package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingTinyDto;
import ru.practicum.shareit.booking.jpaBooking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.jpaUser.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingsService implements BookingsInterface {


    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");


    public BookingsService(ItemRepository itemRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDto createBooking(BookingShortDto bookingDto, Long bookerId) {

        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("даты бронирования должны быть указаны");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("такой вещи нет"));


        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь уже забронирована");
        }

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NotAvailableException("Свою вещь нельзя забронировать");
        }

        boolean hasExistingBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusIn(
                item.getId(), bookerId, List.of(Status.WAITING,Status.APPROVED));

        if (hasExistingBooking) {
            throw new ValidationException("Вы уже отправили заявку на бронирование этой вещи");
        }


        Booking booking = BookingMapper.fromShortBookingDto(bookingDto,user,item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);

        log.info("создание бронирования успешно");
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto approve(Long bookingId,Long userId,Boolean approved) {
        Booking bookingToApprove = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!Objects.equals(bookingToApprove.getItem().getOwner().getId(), userId)) {
            throw new ForbiddenException("Только владелец вещи может согласовать бронирование");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!bookingToApprove.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("статус бронирования не WAITING");
        }

        if (approved) {
            bookingToApprove.setStatus(Status.APPROVED);
        } else {
            bookingToApprove.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingToApprove);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingInfoById(Long bookingId,Long id) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = Objects.equals(booking.getBooker().getId(),id);
        boolean isOwner = Objects.equals(booking.getItem().getOwner().getId(),id);

        if (!isBooker && !isOwner) {
            throw new NotAvailableException("Вы не можете просматривать статус этого бронирования");
        }

        return BookingMapper.toBookingDto(booking);

    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookersBookings(Long userId, State state) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.getBookingsByBookerId(userId, sort);
            case CURRENT -> bookingRepository.getCurrentBookingsByBookerId(userId, LocalDateTime.now(), sort);
            case PAST -> bookingRepository.getBookingsByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
            case FUTURE -> bookingRepository.getBookingsByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
            case WAITING -> bookingRepository.getBookingsByBookerIdAndStatusEquals(userId, Status.WAITING, sort);
            case REJECTED -> bookingRepository.getBookingsByBookerIdAndStatusEquals(userId, Status.REJECTED, sort);
        };

        return bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Long ownerId,State state) {

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        boolean hasItems = itemRepository.existsItemsByOwnerId(ownerId);

        if (!hasItems) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.getBookingsByItemOwnerId(ownerId, sort);
            case CURRENT -> bookingRepository.getCurrentBookingsByItemOwnerId(ownerId, now, sort);
            case PAST -> bookingRepository.getBookingsByItemOwnerIdAndEndBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.getBookingsByItemOwnerIdAndStartAfter(ownerId, now, sort);
            case WAITING -> bookingRepository.getBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING, sort);
            case REJECTED -> bookingRepository.getBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED, sort);
        };

        return bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

    }

    public BookingTinyDto getLastBooking(Long itemId) {
        return bookingRepository
                .findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(
                        itemId,
                        LocalDateTime.now(),
                        List.of(Status.APPROVED)
                )
                .map(booking -> new BookingTinyDto(booking.getId(), booking.getBooker().getId()))
                .orElse(null);
    }

    public BookingTinyDto getNextBooking(Long itemId) {
        return bookingRepository
                .findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(
                        itemId,
                        LocalDateTime.now(),
                        List.of(Status.APPROVED)
                )
                .map(booking -> new BookingTinyDto(booking.getId(), booking.getBooker().getId()))
                .orElse(null);
    }

}
