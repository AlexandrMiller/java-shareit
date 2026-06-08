package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> bookingDtoTester;

    @Autowired
    private JacksonTester<BookingShortDto> bookingShortDtoTester;

    @Autowired
    private JacksonTester<CommentDto> commentDtoTester;

    // --- BookingDto ---

    @Test
    void bookingDto_serializeDates_correctFormat() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2025, 6, 1, 10, 30, 0),
                LocalDateTime.of(2025, 6, 2, 12, 0, 0),
                null,
                null,
                Status.APPROVED
        );

        var result = bookingDtoTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-06-01T10:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-06-02T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("APPROVED");
    }

    @Test
    void bookingDto_deserializeDates_correctParsing() throws Exception {
        String json = """
                {
                  "id": 1,
                  "start": "2025-06-01T10:30:00",
                  "end": "2025-06-02T12:00:00",
                  "status": "WAITING"
                }
                """;

        BookingDto result = bookingDtoTester.parseObject(json);

        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2025, 6, 1, 10, 30, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2025, 6, 2, 12, 0, 0));
        assertThat(result.getStatus()).isEqualTo(Status.WAITING);
    }

    // --- BookingShortDto ---

    @Test
    void bookingShortDto_serializeDates_correctFormat() throws Exception {
        BookingShortDto dto = new BookingShortDto(
                5L,
                LocalDateTime.of(2025, 7, 15, 9, 0, 0),
                LocalDateTime.of(2025, 7, 16, 18, 0, 0)
        );

        var result = bookingShortDtoTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-07-15T09:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-07-16T18:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(5);
    }

    @Test
    void bookingShortDto_deserializeDates_correctParsing() throws Exception {
        String json = """
                {
                  "itemId": 3,
                  "start": "2025-07-15T09:00:00",
                  "end": "2025-07-16T18:00:00"
                }
                """;

        BookingShortDto result = bookingShortDtoTester.parseObject(json);

        assertThat(result.getItemId()).isEqualTo(3L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2025, 7, 15, 9, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2025, 7, 16, 18, 0, 0));
    }

    // --- CommentDto ---

    @Test
    void commentDto_serializeCreated_correctFormat() throws Exception {
        CommentDto dto = new CommentDto(1L, "Отличная вещь", "Иван", LocalDateTime.of(2025, 5, 20, 14, 45, 0));

        var result = commentDtoTester.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-05-20T14:45:00");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличная вещь");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Иван");
    }

    @Test
    void commentDto_deserializeCreated_correctParsing() throws Exception {
        String json = """
                {
                  "id": 2,
                  "text": "Хорошая вещь",
                  "authorName": "Мария",
                  "created": "2025-05-20T14:45:00"
                }
                """;

        CommentDto result = commentDtoTester.parseObject(json);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getText()).isEqualTo("Хорошая вещь");
        assertThat(result.getAuthorName()).isEqualTo("Мария");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2025, 5, 20, 14, 45, 0));
    }
}
