package ru.reminder.app.integration.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.dto.ReminderDto;
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.model.entity.User;
import ru.reminder.app.repository.ReminderRepository;
import ru.reminder.app.repository.UserRepository;
import ru.reminder.app.service.ReminderService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReminderServiceIT {

    private static final Long REMINDER_ID = 1L;
    private static final Long USER_ID = 1L;

    @Autowired
    ReminderRepository reminderRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    ReminderService reminderService;

    @Test
    void getReminderById_shouldReturnReminderIfExists() {
        User user = new User("username", "password");
        userRepo.save(user);
        Reminder reminderEntity = Reminder.builder()
                .title("Заголовок")
                .description("Описание")
                .remind(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .user(user)
                .build();
        Reminder savedReminder = reminderRepo.save(reminderEntity);
        ReminderDto found = reminderService.getReminderById(savedReminder.getId());

        assertNotNull(found);
        assertAll("Reminder fields mapping",
                () -> assertEquals(savedReminder.getTitle(), found.getTitle(), "Title mismatch"),
                () -> assertEquals(savedReminder.getDescription(), found.getDescription(), "Description mismatch"),
                () -> assertEquals(savedReminder.getRemind(), found.getRemind(), "Reminder time mismatch"),
                () -> assertEquals(savedReminder.getUser().getId(), found.getUserId(), "User ID mismatch"));

    }

    @Test
    void getReminderById_shouldThrowException_whenNotFound() {
        Long nonExistentId = 100000L;
        BusinessException thrown = Assertions.assertThrows(BusinessException.class, () -> {
            reminderService.getReminderById(nonExistentId);
        });

        assertAll("Exception details",
                () -> assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus()),
                () -> assertEquals("Reminder with id " + nonExistentId + " not found", thrown.getMessage())
        );

    }

    @Test
    void deleteById_shouldActuallyRemoveReminderFromDatabase() {
        User user = new User("username", "password");
        userRepo.save(user);

        Reminder reminder = Reminder.builder()
                .title("Удалить меня")
                .user(user)
                .remind(LocalDateTime.now())
                .build();
        Reminder saved = reminderRepo.save(reminder);
        Long id = saved.getId();
        reminderService.deleteById(id);

        Optional<Reminder> deleted =reminderRepo.findById(id);
        assertTrue(deleted.isEmpty());


    }
}
