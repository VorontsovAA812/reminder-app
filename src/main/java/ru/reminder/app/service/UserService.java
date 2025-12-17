package ru.reminder.app.service;

import ru.reminder.app.REST.DTO.UserDto;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.User;
import ru.reminder.app.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow( BusinessException.of(HttpStatus.NOT_FOUND,
                "User with id " + id + " not found"
        ));
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Long createUser(UserDto userDto) {
        User user = new User(userDto.getUserName(), userDto.getPassword());
       return  userRepository.save(user).getId();
    }

    public void  deleteById(Long id) {
       User user =  getUserById(id);
       userRepository.delete(user);
    }
}