package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String HEADER = "X-Sharer-User-Id";

    // --- POST /bookings ---

    @Test
    void bookItem_whenValid_returns200() throws Exception {
        BookingShortDto dto = new BookingShortDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingClient.bookItem(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(eq(1L), any());
    }

    @Test
    void bookItem_whenStartIsNull_returns400() throws Exception {
        BookingShortDto dto = new BookingShortDto();
        dto.setItemId(1L);
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_whenEndIsNull_returns400() throws Exception {
        BookingShortDto dto = new BookingShortDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_whenNoHeader_returns400() throws Exception {
        BookingShortDto dto = new BookingShortDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /bookings/{bookingId} ---

    @Test
    void getBooking_returns200() throws Exception {
        when(bookingClient.getBooking(eq(1L), eq(2L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/2")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(eq(1L), eq(2L));
    }

    // --- PATCH /bookings/{bookingId} ---

    @Test
    void approveBooking_returns200() throws Exception {
        when(bookingClient.approveBooking(eq(1L), eq(2L), eq(true))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/2")
                        .header(HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(eq(1L), eq(2L), eq(true));
    }

    // --- GET /bookings ---

    @Test
    void getBookings_withDefaultState_returns200() throws Exception {
        when(bookingClient.getBookings(eq(1L), any(), eq(0), eq(10))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(eq(1L), any(), eq(0), eq(10));
    }

    @Test
    void getBookings_withUnknownState_returns400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "UNKNOWN_STATE"))
                .andExpect(status().isBadRequest());
    }

    // --- GET /bookings/owner ---

    @Test
    void getOwnerBookings_withDefaultState_returns200() throws Exception {
        when(bookingClient.getOwnerBookings(eq(1L), any(), eq(0), eq(10))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(eq(1L), any(), eq(0), eq(10));
    }

    @Test
    void getOwnerBookings_withUnknownState_returns400() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "WRONG"))
                .andExpect(status().isBadRequest());
    }
}
