package org.gladyshev.ru.userservice1.Service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.gladyshev.ru.userservice1.Dto.UserDTO;
import org.gladyshev.ru.userservice1.Entity.User;
import org.gladyshev.ru.userservice1.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
     UserRepository userRepository;

    @InjectMocks
     UserService userService;

    @Test
    public void returnAll_returnUsers(){
        var users = List.of(new User(1,"Alex","Alex123@gmail.com"),new User(2,"Petr", "Petr123@gmail.com"));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Assertions.assertEquals(users,userService.findAll());
    }

    @Test
    public void returnAll_returnUsers_throwNotFoundException(){
        List<User> users = Collections.emptyList();
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Assertions.assertThrows(EntityNotFoundException.class, ()-> userService.findAll());
    }

    @Test
    public void UserByName_returnUser(){
        User user = new User(1,"Alex","Alex123@gmail.com");

        Mockito.when(userRepository.findByName(user.getName())).thenReturn(Optional.of(user));

        Assertions.assertEquals("Alex",userService.findByUsername(user.getName()).getName());

    }

    @Test
    public void UserByName_NotFound(){
        String name = "Alex";
        Mockito.when(userRepository.findByName(name)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findByUsername(name));
    }

    @Test
    public void findById_returnUser(){
        int id = 1;
        User user = new User();
        user.setId(id);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Assertions.assertEquals(user.getId(),userService.findById(1).getId());
    }

    @Test
    public void findById_NotFound(){
        User user = new User();
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findById(1));
    }

    @Test
    public void createUser_saveUser(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByName(userDTO.getName())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        userService.createUser(userDTO);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        Assertions.assertEquals(userDTO.getName(),savedUser.getName());
        Assertions.assertEquals(userDTO.getEmail(),savedUser.getEmail());

    }

    @Test
    public void createUser_findByNameException(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByName(userDTO.getName())).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class,() -> userService.createUser(userDTO));
        verify(userRepository,Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void createUser_findByEmailException(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDTO));
        verify(userRepository,Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void deleteUserById_deleteUser(){
        int id = 1;
        Mockito.when(userRepository.existsById(id)).thenReturn(true);
        userService.deleteById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    public void deleteUserById_notFoundUserException(){
        int id = 2;
        Mockito.when(userRepository.existsById(id)).thenReturn(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.deleteById(id));
        verify(userRepository,Mockito.never()).deleteById(id);
    }

    @Test
    public void saveUser_updateUser(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByName(userDTO.getName())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        userService.save(userDTO);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        Assertions.assertEquals(userDTO.getName(),savedUser.getName());
        Assertions.assertEquals(userDTO.getEmail(),savedUser.getEmail());
    }

    @Test
    public void saveUser_existByNameException(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByName(userDTO.getName())).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class,() -> userService.save(userDTO));
        verify(userRepository,Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    public void saveUser_existByEmailException(){
        UserDTO userDTO = new UserDTO("Alex","Alex123@gmail.com");
        Mockito.when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class,() -> userService.save(userDTO));
        verify(userRepository,Mockito.never()).save(Mockito.any(User.class));
    }


}