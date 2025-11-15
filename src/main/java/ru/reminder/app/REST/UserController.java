package ru.reminder.app.REST;

import ru.reminder.app.REST.DTO.UserDto;
import ru.reminder.app.model.User;
import ru.reminder.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {

        User user = new User(userDto.getUserName(), userDto.getPassword());
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully");
    }
}