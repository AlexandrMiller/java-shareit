package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingTinyDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),booking.getStart(),booking.getEnd(),
                booking.getItem(),booking.getBooker(),booking.getStatus());
    }

    public static Booking fromShortBookingDto(BookingShortDto bookingShortDto, User user, Item item) {
        return new Booking(null,bookingShortDto.getStart(),bookingShortDto.getEnd(),item,user, Status.WAITING);
    }

    public static BookingTinyDto tinyDto(Booking booking) {
        return new BookingTinyDto(booking.getId(),booking.getBooker().getId());
    }
}
