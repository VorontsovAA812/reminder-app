package ru.reminder.app.REST;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import ru.reminder.app.REST.DTO.ReminderDto;
import ru.reminder.app.REST.DTO.ReminderResponse;
import ru.reminder.app.service.ReminderService;


@RestController
@RequestMapping("/api/v1/reminder")
public class ReminderController {

    private final ReminderService reminderService;


    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping("/create")
    public ResponseEntity<ReminderResponse> createReminder(@RequestBody ReminderDto reminderDto) {

        ReminderResponse reminderResponse = reminderService.createReminder(reminderDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderResponse);
    }

    @GetMapping("/{id}")
    public ReminderResponse getReminderById(@PathVariable Long id) {
        return reminderService.getReminderById(id);
    }

    @DeleteMapping(("/{id}"))
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        reminderService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
