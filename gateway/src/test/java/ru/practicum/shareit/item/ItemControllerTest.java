package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void postItem_whenValid_returns200() throws Exception {
        ItemDto dto = new ItemDto("Дрель", "Мощная дрель", true, null);
        when(itemClient.postItem(any(), eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).postItem(any(), eq(1L));
    }

    @Test
    void postItem_whenNoHeader_returns400() throws Exception {
        ItemDto dto = new ItemDto("Дрель", "Описание", true, null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_returns200() throws Exception {
        ItemDto dto = new ItemDto("Новое имя", null, null, null);
        when(itemClient.updateItem(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/2")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(1L), eq(2L), any());
    }

    @Test
    void getItemById_returns200() throws Exception {
        when(itemClient.getItemById(eq(1L), eq(2L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/2")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(eq(1L), eq(2L));
    }

    @Test
    void getItemById_whenNoHeader_returns400() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByOwner_returns200() throws Exception {
        when(itemClient.getItemsByOwner(eq(1L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemClient).getItemsByOwner(eq(1L));
    }

    @Test
    void searchItem_returns200() throws Exception {
        when(itemClient.searchItem("дрель")).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk());

        verify(itemClient).searchItem("дрель");
    }

    @Test
    void searchItem_whenNoParam_returns400() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_returns200() throws Exception {
        CommentDto dto = new CommentDto(null, "Отличная вещь", null, null);
        when(itemClient.createComment(any(), eq(1L), eq(2L))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/2/comment")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).createComment(any(), eq(1L), eq(2L));
    }
}
