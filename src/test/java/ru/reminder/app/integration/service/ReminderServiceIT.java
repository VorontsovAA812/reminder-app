package ru.reminder.app.integration.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.dto.PagingResult;
import ru.reminder.app.model.dto.ReminderDto;
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.model.entity.User;
import ru.reminder.app.repository.ReminderRepository;
import ru.reminder.app.repository.UserRepository;
import ru.reminder.app.service.ReminderService;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReminderServiceIT {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    ReminderRepository reminderRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    ReminderService reminderService;



    @Test
    void getReminderById_shouldReturnReminderIfExists() {
        User user = new User(USERNAME, PASSWORD);
        userRepo.save(user);
        Reminder reminderEntity = Reminder.builder()
                .title("Заголовок")
                .description("Описание")
                .remind(now().truncatedTo(ChronoUnit.MICROS))
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
        BusinessException thrown = assertThrows(BusinessException.class, () ->
                reminderService.getReminderById(nonExistentId));

        assertAll("Exception details",
                () -> assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus()),
                () -> assertEquals("Reminder with id " + nonExistentId + " not found", thrown.getMessage())
        );

    }

    @Test
    void deleteById_shouldActuallyRemoveReminderFromDatabase() {
        User user = new User(USERNAME, PASSWORD);
        userRepo.save(user);
        Reminder reminder = Reminder.builder()
                .title("Удалить меня")
                .user(user)
                .remind(now())
                .build();
        Reminder saved = reminderRepo.save(reminder);
        Long id = saved.getId();
        reminderService.deleteById(id);

        Optional<Reminder> deleted = reminderRepo.findById(id);
        assertTrue(deleted.isEmpty());
    }

    @Test
    void deleteReminder_shouldThrowException_whenNotFound() {
        Long nonExistentId = 100000L;
        BusinessException thrown = assertThrows(BusinessException.class, () ->
            reminderService.deleteById(nonExistentId));
        assertAll("Exception details",
                () -> assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus()),
                () -> assertEquals("Reminder with id " + nonExistentId + " not found", thrown.getMessage())
        );
    }

    @Test
    void createReminder_shouldCorrectSave() {
        User user = new User(USERNAME, PASSWORD);
        Long id = userRepo.save(user).getId();

        ReminderDto reminderDto = ReminderDto.builder()
                .title("Купить хлеб")
                .userId(id)
                .description("хлеб выбирать свежий")
                .remind(now().truncatedTo(ChronoUnit.MICROS))
                .build();
        ReminderDto created = reminderService.createReminder(reminderDto);

        Optional<Reminder> found = reminderRepo.findUserReminder(id, created.getRemind());

        Reminder element;
        if (found.isPresent()) {
            element = found.get();
        } else {
            element = null;
            fail("Reminder не найден");
        }
        assertAll("Details",
                () -> assertEquals(created.getTitle(), element.getTitle()),
                () -> assertEquals(created.getUserId(), element.getUser().getId()),
                () -> assertEquals(created.getDescription(), element.getDescription()),
                () -> assertEquals(created.getRemind(), element.getRemind()));
    }

    @Test
    void createReminder_shouldReturnCorrectResponse() {
        User user = new User(USERNAME, PASSWORD);
        Long id = userRepo.save(user).getId();
        ReminderDto reminderDto = ReminderDto.builder()
                .title("Купить хлеб")
                .userId(id)
                .description("хлеб выбирать свежий")
                .remind(now().truncatedTo(ChronoUnit.MICROS))
                .build();
        ReminderDto created = reminderService.createReminder(reminderDto);
        assertNotNull(created);
        assertAll("Details",
                () -> assertEquals(created.getTitle(), reminderDto.getTitle()),
                () -> assertEquals(created.getUserId(), reminderDto.getUserId()),
                () -> assertEquals(created.getDescription(), reminderDto.getDescription()),
                () -> assertEquals(created.getRemind(), reminderDto.getRemind()));
    }

    @Test
    void findAll_ShouldReturnPaginatedResult() {
        User user = new User(USERNAME, PASSWORD);
        User savedUser = userRepo.save(user);
        Reminder r1 = reminderRepo.save(Reminder.builder().title("Task 1").user(user).remind(now().plusDays(1)).build());
        Reminder r2 = reminderRepo.save(Reminder.builder().title("Task 2").user(user).remind(now().plusDays(2)).build());
        reminderRepo.save(Reminder.builder().title("Task 3").user(user).remind(now().plusDays(3)).build());
        int pageNum = 1;
        int size = 2;
        int total = 2;
        String sortBy = "date";
        Boolean toDay = false;

        PagingResult<ReminderDto> reminders = reminderService.findAll(pageNum, size, savedUser.getId(), sortBy, toDay);

        assertThat(reminders.getContent())
                .usingRecursiveComparison()
                .ignoringFields("userId", "user", "id")
                .isEqualTo(List.of(r1, r2));
        assertEquals(total, reminders.getTotal());

    }
}
