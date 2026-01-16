package ru.reminder.app.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.dto.PagingResult;
import ru.reminder.app.model.dto.ReminderDto;
import ru.reminder.app.model.dto.ReminderResponse;
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.query.SortingOptions;
import ru.reminder.app.repository.ReminderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepo;
    private final UserService userService;

    public ReminderResponse createReminder(ReminderDto reminderDto) {
        Reminder reminder = new Reminder(
                reminderDto.getTitle(),
                reminderDto.getDescription(),
                reminderDto.getRemind()
        );
        // Временная заглушка, пока не добавлена аутентификация
        reminder.setUser(userService.getUserById(reminderDto.getUserId()));

        Reminder savedReminder = reminderRepo.save(reminder);

        return ReminderResponse.builder()
                .id(savedReminder.getId())
                .title(savedReminder.getTitle())
                .description(savedReminder.getDescription())
                .remind(savedReminder.getRemind())
                .userId(savedReminder.getUser().getId())
                .build();
    }


    public ReminderResponse getReminderById(Long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(BusinessException.of(HttpStatus.NOT_FOUND,
                        "Reminder with id " + id + " not found"));

        return ReminderResponse.builder()
                .id(reminder.getId())
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .remind(reminder.getRemind())
                .userId(reminder.getUser().getId())
                .build();
    }

    public void deleteById(Long id) {
        ReminderResponse reminder = getReminderById(id);

        reminderRepo.deleteById(reminder.getId());
    }

    public PagingResult<ReminderDto> findAll(Integer page, Integer size, Long userId, String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, size, SortingOptions.valueOf(sortBy.toUpperCase()).getSort());
        Page<Reminder> entities = reminderRepo.findByUserId(userId, pageable);

        List<ReminderDto> entitiesDto = entities.stream()
                .map(entity -> ReminderDto.builder()
                        .title(entity.getTitle())
                        .description(entity.getDescription())
                        .remind(entity.getRemind())
                        .userId(userId)
                        .build())
                .collect(Collectors.toList());

        return PagingResult.<ReminderDto>builder()
                .content(entitiesDto)
                .total(entities.getTotalPages())
                .current(entities.getNumber() + 1)
                .build();
    }


}
