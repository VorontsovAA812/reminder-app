package ru.reminder.app.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.reminder.app.model.dto.PagingResult;
import ru.reminder.app.model.dto.ReminderDto;
import ru.reminder.app.model.dto.ReminderResponse;
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

    @GetMapping("/list")
    public PagingResult<ReminderDto> findAllReminders(

            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "2") Integer size,
            @RequestParam(defaultValue = "1") Long userId, // ← ВРЕМЕННО! пока не добавлена ауентификация
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(required = false) Boolean today) {

        return reminderService.findAll(page, size, userId, sortBy, today);
    }

}
