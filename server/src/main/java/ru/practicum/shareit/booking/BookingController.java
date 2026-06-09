package ru.practicum.shareit.booking;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingsService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingsService bookingsService;

    public BookingController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingShortDto bookingDto, @RequestHeader(HEADER) Long bookerId) {
        return bookingsService.createBooking(bookingDto,bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestHeader(HEADER) Long userId,
                              @RequestParam(name = "approved") Boolean approved) {
        return bookingsService.approve(bookingId,userId,approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfoById(@PathVariable Long bookingId,@RequestHeader(HEADER) Long id) {
        return bookingsService.getBookingInfoById(bookingId,id);
    }

    @GetMapping
    public List<BookingDto> getBookersBookings(@RequestHeader(HEADER) Long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingsService.getBookersBookings(userId,state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(HEADER) Long ownerId,
                                               @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingsService.getBookingsByOwner(ownerId,state);
    }
}
