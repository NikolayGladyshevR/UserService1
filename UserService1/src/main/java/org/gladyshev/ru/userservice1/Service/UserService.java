package org.gladyshev.ru.userservice1.Service;

import org.gladyshev.ru.userservice1.Dto.UserDTO;
import org.gladyshev.ru.userservice1.Entity.User;
import org.gladyshev.ru.userservice1.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public User findByUsername(String username) {
        return userRepository.findByName(username);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void createUser(UserDTO userDTO) {
        if(userRepository.existsByName(userDTO.getName())) {
            throw new IllegalArgumentException("Данный логин уже занят");
        }
        if(userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Данный email уже используется");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
    }

    @Transactional
    public void save(UserDTO userDTO) {
        if(userRepository.existsByName(userDTO.getName())) {
            throw new IllegalArgumentException("Данный логин уже занят");
        }
        if(userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Данный email уже используется");
        }
        User user = new User();
        user.setName(user.getName());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
    }
    @Transactional
    public void deleteById(Integer id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }else{
            throw new IllegalArgumentException("Id not found");
        }

    }
}
