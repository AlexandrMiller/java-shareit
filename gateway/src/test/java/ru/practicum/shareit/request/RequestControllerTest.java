package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private static final String HEADER = "X-Sharer-User-Id";

    // --- POST /requests ---

    @Test
    void createRequest_whenValid_returns200() throws Exception {
        RequestDto dto = new RequestDto(null, "Нужна дрель", null, null);
        when(requestClient.postRequest(any(), eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(requestClient).postRequest(any(), eq(1L));
    }

    @Test
    void createRequest_whenDescriptionIsBlank_returns400() throws Exception {
        RequestDto dto = new RequestDto(null, "", null, null);

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_whenDescriptionIsNull_returns400() throws Exception {
        RequestDto dto = new RequestDto(null, null, null, null);

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_whenNoHeader_returns400() throws Exception {
        RequestDto dto = new RequestDto(null, "Нужна дрель", null, null);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /requests ---

    @Test
    void getMyRequests_returns200() throws Exception {
        when(requestClient.getMyRequests(eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(requestClient).getMyRequests(eq(1L));
    }

    @Test
    void getMyRequests_whenNoHeader_returns400() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    // --- GET /requests/all ---

    @Test
    void getAllRequests_returns200() throws Exception {
        when(requestClient.getAllRequests(eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests(eq(1L));
    }

    // --- GET /requests/{requestId} ---

    @Test
    void getRequestById_returns200() throws Exception {
        when(requestClient.getRequestById(eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk());

        verify(requestClient).getRequestById(eq(1L));
    }
}
