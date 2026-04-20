package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingsInterface {

    BookingDto createBooking(BookingShortDto bookingDto, Long bookerId);

    BookingDto approve(Long bookingId,Long userId,Boolean approved);

    BookingDto getBookingInfoById(Long bookingId,Long id);

    List<BookingDto> getBookersBookings(Long userId, State state);

    List<BookingDto> getBookingsByOwner(Long ownerId,State state);
}
