package ru.practicum.shareit.booking.jpaBooking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> getBookingsByBookerId(Long userId, Sort sort);

    List<Booking> getCurrentBookingsByBookerId(Long userId, LocalDateTime now, Sort sort);

    List<Booking> getBookingsByBookerIdAndEndBefore(Long userId, LocalDateTime end, Sort sort);

    List<Booking> getBookingsByBookerIdAndStartAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> getBookingsByBookerIdAndStatusEquals(Long userId, Status status, Sort sort);

    List<Booking> getBookingsByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> getBookingsByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> getBookingsByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> getCurrentBookingsByItemOwnerId(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> getBookingsByItemOwnerIdAndStatusEquals(Long ownerId,Status status, Sort sort);

    Optional<Booking> findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(Long itemId,
                                                                             LocalDateTime startAfter,
                                                                             List<Status> statuses);//nextBooking

    Optional<Booking> findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(Long itemId,
                                                                           LocalDateTime finishBefore,
                                                                           List<Status> statuses);//lastBooking

    boolean existsByItemIdAndBookerIdAndStatusIn(Long itemId, Long bookerId, List<Status> statuses);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, Status status, LocalDateTime now);
}
