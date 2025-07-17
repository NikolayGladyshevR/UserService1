package org.gladyshev.ru.userservice1.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.gladyshev.ru.userservice1.Dto.UserDTO;
import org.gladyshev.ru.userservice1.Entity.User;
import org.gladyshev.ru.userservice1.Exceptions.GlobalExceptionHandler;
import org.gladyshev.ru.userservice1.Service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;


import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private final User user1 = new  User(1, "Alex", "Alex123@gmail.com");
    private final User user2 = new  User(2, "Petr", "Petr123@gmail.com");


    @Test
    void getAllUsers_success() throws Exception {
        var users = List.of(user1, user2);
        when(userService.findAll()).thenReturn(users);
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));

        verify(userService, times(1)).findAll();

    }

    @Test
    void getAllUsers_failed() throws Exception {
        when(userService.findAll()).thenThrow(new EntityNotFoundException("Пользователей нет"));
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователей нет"));

        verify(userService, times(1)).findAll();

    }


    @Test
    void getAllUsers_failure() throws Exception {
        //# Условие, вызывается метод сервиса, который выбрасывает ошибку, что пользователей нет
        when(userService.findAll()).thenThrow(new EntityNotFoundException("Пользователей нет"));
        mockMvc.perform(get("/api/users")).andExpect(status().isNotFound())
                                                 .andExpect(jsonPath("$.error").value("Пользователей нет"));
    }



    @Test
    void addUser_success() throws Exception {
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");

        doNothing().when(userService).createUser(any(UserDTO.class));

        mockMvc.perform(post("/api/users/add")
                .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("Пользователь добавлен"));

        verify(userService, times(1)).createUser(any(UserDTO.class));

    }

    @Test
    void addUser_failedByName( ) throws Exception {
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        doThrow(new IllegalArgumentException("Данный логин уже занят")).when(userService).createUser(any(UserDTO.class));
        mockMvc.perform(post("/api/users/add").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Данный логин уже занят"));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void addUser_failedByEmail( ) throws Exception {
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        doThrow(new IllegalArgumentException("Данный email уже используется")).when(userService).createUser(any(UserDTO.class));
        mockMvc.perform(post("/api/users/add").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Данный email уже используется"));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }


    @Test
    void updateUser_success() throws Exception {
        UserDTO userDTOtoUpdate = new UserDTO("Petr","Petr123@gmail.com");
        doNothing().when(userService).save(any(UserDTO.class));
        mockMvc.perform(patch("/api/users/" + user1.getId())
        .contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTOtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Изменения успешно внесены"));


        verify(userService, times(1)).save(any(UserDTO.class));
    }

    @Test
    void updateUser_failedByName()  throws Exception {
        UserDTO userDTOtoUpdate = new UserDTO("Alex","Petr123@gmail.com");
        doThrow(new IllegalArgumentException("Данный логин уже занят")).when(userService).save(any(UserDTO.class));
        mockMvc.perform(patch("/api/users/" + user1.getId()).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTOtoUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Данный логин уже занят"));
        verify(userService, times(1)).save(any(UserDTO.class));
    }

    @Test
    void updateUser_failedByEmail() throws Exception {
        UserDTO userDTOtoUpdate = new UserDTO("Alex","Petr123@gmail.com");
        doThrow(new IllegalArgumentException("Данный email уже используется")).when(userService).save(any(UserDTO.class));
        mockMvc.perform(patch(("/api/users/" + user1.getId())).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTOtoUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Данный email уже используется"));
        verify(userService, times(1)).save(any(UserDTO.class));
    }




}