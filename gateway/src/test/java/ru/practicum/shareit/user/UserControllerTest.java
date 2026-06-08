package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    // --- POST /users ---

    @Test
    void createUser_whenValid_returns200() throws Exception {
        UserDto dto = new UserDto("Иван", "ivan@mail.ru");
        when(userClient.createUser(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).createUser(any());
    }

    @Test
    void createUser_whenNameIsBlank_returns400() throws Exception {
        UserDto dto = new UserDto("", "ivan@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenEmailIsInvalid_returns400() throws Exception {
        UserDto dto = new UserDto("Иван", "not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenEmailIsBlank_returns400() throws Exception {
        UserDto dto = new UserDto("Иван", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /users/{id} ---

    @Test
    void getUserById_returns200() throws Exception {
        when(userClient.getUserById(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).getUserById(1L);
    }

    // --- DELETE /users/{id} ---

    @Test
    void deleteUser_returns200() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }

    // --- PATCH /users/{id} ---

    @Test
    void updateUser_returns200() throws Exception {
        UserDto dto = new UserDto("Новое имя", "new@mail.ru");
        when(userClient.updateUser(eq(1L), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(1L), any());
    }
}
