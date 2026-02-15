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
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.query.SortingOptions;
import ru.reminder.app.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepo;
    private final UserService userService;

    public ReminderDto createReminder(ReminderDto reminderDto) {
        Reminder reminder = Reminder.builder()
                .title(reminderDto.getTitle())
                .description(reminderDto.getDescription())
                .remind(reminderDto.getRemind().truncatedTo(ChronoUnit.MINUTES))
                .user(userService.getUserById(reminderDto.getUserId()))
                .build();

        Reminder savedReminder = reminderRepo.save(reminder);

        return convertingReminderToDTO(savedReminder);
    }

    public ReminderDto getReminderById(Long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(BusinessException.of(HttpStatus.NOT_FOUND,
                        "Reminder with id " + id + " not found"));

        return convertingReminderToDTO(reminder);
    }

    public void deleteById(Long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Reminder with id " + id + " not found"));

        reminderRepo.deleteById(reminder.getId());
    }


    public PagingResult<ReminderDto> findAll(Integer pageNum, Integer size, Long userId, String sortBy, Boolean today) {
        Pageable pageable = PageRequest.of(pageNum - 1, size, SortingOptions.valueOf(sortBy.toUpperCase()).getSort());
        Page<Reminder> page = toDayFilter(userId, pageable, today);

        return toPagingResult(page, userId);

    }

    private Page<Reminder> toDayFilter(Long userId, Pageable pageable, Boolean today) {
        if (today != null && today) {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

            return reminderRepo.findRemindBetweenDates(userId, startOfDay, endOfDay, pageable);

        }
        return reminderRepo.findByUserId(userId, pageable);
    }

    private PagingResult<ReminderDto> toPagingResult(Page<Reminder> page, Long userId) {
        List<ReminderDto> contentDto = page.stream()
                .map(reminder -> ReminderDto.builder()
                        .title(reminder.getTitle())
                        .description(reminder.getDescription())
                        .remind(reminder.getRemind())
                        .userId(userId)
                        .build())
                .collect(Collectors.toList());

        return PagingResult.<ReminderDto>builder()
                .content(contentDto)
                .total(page.getTotalPages())
                .current(page.getNumber() + 1)
                .build();
    }

    private ReminderDto convertingReminderToDTO(Reminder reminder) {
        return ReminderDto.builder()
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .remind(reminder.getRemind())
                .userId(reminder.getUser().getId())
                .build();
    }


}
