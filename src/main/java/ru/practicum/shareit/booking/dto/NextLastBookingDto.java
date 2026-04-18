package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class NextLastBookingDto {

    private BookingTinyDto nextBooking;

    private BookingTinyDto lastBooking;
}
