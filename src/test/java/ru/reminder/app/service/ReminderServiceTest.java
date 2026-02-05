package ru.reminder.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.dto.PagingResult;
import ru.reminder.app.model.dto.ReminderDto;
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.model.entity.User;
import ru.reminder.app.query.SortingOptions;
import ru.reminder.app.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {
    private static final Long REMINDER_ID = 1L;
    private static final Long USER_ID = 1L;

    @Mock
    private ReminderRepository reminderRepo;

    @InjectMocks
    private ReminderService reminderService;

    @Test
    void getReminderById_shouldReturnReminderIfExists() {
        User user = new User();
        user.setId(USER_ID);
        Reminder reminderEntity = Reminder.builder()
                .id(REMINDER_ID)
                .title("Заголовок")
                .description("Описание")
                .remind(LocalDateTime.now())
                .user(user)
                .build();
        when(reminderRepo.findById(REMINDER_ID)).thenReturn(Optional.of(reminderEntity));

        ReminderDto found = reminderService.getReminderById(REMINDER_ID);

        assertNotNull(found);
        assertAll("Reminder fields mapping",
                () -> assertEquals(reminderEntity.getTitle(), found.getTitle(), "Title mismatch"),
                () -> assertEquals(reminderEntity.getDescription(), found.getDescription(), "Description mismatch"),
                () -> assertEquals(reminderEntity.getRemind(), found.getRemind(), "Reminder time mismatch"),
                () -> assertEquals(user.getId(), found.getUserId(), "User ID mismatch")
        );
        verify(reminderRepo, times(1)).findById(REMINDER_ID);
    }

    @Test
    void getReminderById_shouldThrowException_whenNotFound() {
        Long nonExistentId = 100000L;
        when(reminderRepo.findById(nonExistentId)).thenReturn(Optional.empty());
        BusinessException thrown = Assertions.assertThrows(BusinessException.class, () -> {
            reminderService.getReminderById(nonExistentId);
        });

        assertAll("Exception details",
                () -> assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus()),
                () -> assertEquals("Reminder with id " + nonExistentId + " not found", thrown.getMessage())
        );
        verify(reminderRepo, times(1)).findById(nonExistentId);

    }

    @Test
    void deleteReminder_shouldCallRepositoryDeleteById() {
        Reminder reminder = new Reminder();
        reminder.setId(REMINDER_ID);
        when(reminderRepo.findById(REMINDER_ID)).thenReturn(Optional.of(reminder));

        reminderService.deleteById(REMINDER_ID);

        verify(reminderRepo, times(1)).findById(REMINDER_ID);
        verify(reminderRepo, times(1)).deleteById(REMINDER_ID);
    }

    @Test
    void deleteReminder_shouldThrowException_whenNotFound() {
        Long nonExistentId = 100000L;
        when(reminderRepo.findById(nonExistentId)).thenReturn(Optional.empty());
        BusinessException thrown = Assertions.assertThrows(BusinessException.class, () -> {
            reminderService.deleteById(nonExistentId);
        });

        assertAll("Exception details",
                () -> assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus()),
                () -> assertEquals("Reminder with id " + nonExistentId + " not found", thrown.getMessage())
        );
        verify(reminderRepo, times(1)).findById(nonExistentId);
        verify(reminderRepo, never()).deleteById(anyLong());
    }

    @Test
    void createReminder_shouldSaveEntity_AndReturnResponse() {
        User user = new User();
        user.setId(USER_ID);
        Reminder reminder = Reminder.builder()
                .title("Купить хлеб")
                .user(user)
                .build();
        when(reminderRepo.save(any(Reminder.class))).thenReturn(reminder);
        ReminderDto reminderDto = ReminderDto.builder()
                .title("Купить хлеб")
                .userId(USER_ID)
                .build();

        ReminderDto created = reminderService.createReminder(reminderDto);

        assertNotNull(created);
        assertEquals(USER_ID, created.getUserId());
        assertEquals("Купить хлеб", created.getTitle());
        verify(reminderRepo, times(1)).save(any(Reminder.class));
    }

    @Test
    void findAll_ShouldReturnPaginatedResult() {
        int pageNum = 1;
        int size = 2;
        int total = 2;
        String sortBy = "date";
        Boolean toDay = false;

        List<Reminder> lst = List.of(Reminder.builder().title("Купить хлеб").build());
        Pageable pageable = PageRequest.of(pageNum - 1, size, SortingOptions.DATE.getSort());
        Page<Reminder> page = new PageImpl<>(lst, pageable, total);
        when(reminderRepo.findByUserId(USER_ID, pageable)).thenReturn(page);

        PagingResult<ReminderDto> reminders = reminderService.findAll(pageNum, size, USER_ID, sortBy, toDay);

        assertEquals(lst.get(0).getTitle(), new ArrayList<>(reminders.getContent()).get(0).getTitle());
        assertEquals(lst.size(), reminders.getContent().size());
        assertEquals(pageNum, reminders.getCurrent());
        verify(reminderRepo, times(1)).findByUserId(USER_ID, pageable);
    }

    @Test
    void findAll_WithTodayTrue_ShouldUseTodaySearch() {
        int pageNum = 1;
        int size = 10;
        String sortBy = "date";
        Boolean toDayFilter = true;
        when(reminderRepo.findRemindBetweenDates(anyLong(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        reminderService.findAll(pageNum, size, USER_ID, sortBy, toDayFilter);

        verify(reminderRepo, times(1)).findRemindBetweenDates(eq(USER_ID), any(), any(), any(Pageable.class));
        verify(reminderRepo, never()).findByUserId(eq(USER_ID), any(Pageable.class));
    }

    @Test
    void findAll_WithTodayFalse_ShouldUseDefaultSearch() {
        int pageNum = 1;
        int size = 10;
        String sortBy = "date";
        Boolean toDayFilter = false;
        when(reminderRepo.findByUserId(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        reminderService.findAll(pageNum, size, USER_ID, sortBy, toDayFilter);

        verify(reminderRepo, times(1)).findByUserId(eq(USER_ID), any(Pageable.class));
        verify(reminderRepo, never()).findRemindBetweenDates(eq(USER_ID), any(), any(), any(Pageable.class));
    }
}