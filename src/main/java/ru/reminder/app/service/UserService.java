package ru.reminder.app.service;

import org.springframework.transaction.annotation.Transactional;
import ru.reminder.app.model.dto.UserDto;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.entity.User;
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
        return userRepository.findById(id).orElseThrow(BusinessException.of(HttpStatus.NOT_FOUND,
                "User with id " + id + " not found"
        ));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Long createUser(UserDto userDto) {
        User user = new User(userDto.getUserName(), userDto.getPassword());
        return userRepository.save(user).getId();
    }

    public void deleteById(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Transactional
    public User setEmail(Long userId, String email) {
        User user = getUserById(userId);
        user.setEmail(email);
        return userRepository.save(user);
    }
}