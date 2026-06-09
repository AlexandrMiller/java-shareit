package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.util.DateConstanta;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingShortDto {

    private Long itemId;

    @JsonFormat(pattern = DateConstanta.dateCon)
    private LocalDateTime start;

    @JsonFormat(pattern = DateConstanta.dateCon)
    private LocalDateTime end;

}
