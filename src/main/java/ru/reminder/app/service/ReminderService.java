package ru.reminder.app.service;

import ru.reminder.app.REST.DTO.ReminderDto;
import org.springframework.stereotype.Service;
import ru.reminder.app.REST.DTO.ReminderResponse;
import ru.reminder.app.model.Reminder;
import ru.reminder.app.repository.ReminderRepository;


@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserService userService;

    public ReminderService(ReminderRepository reminderRepository, UserService userService) {
        this.reminderRepository = reminderRepository;
        this.userService = userService;
    }

    public ReminderResponse createReminder(ReminderDto reminderDto) {

        // собираем объект напоминания
        Reminder reminder = new Reminder(reminderDto.getTitle(), reminderDto.getDescription(),reminderDto.getRemind());
        reminder.setUser(userService.getUserById(reminderDto.getUserId())); // временно, пока не добавлена ауентификация

        // собираем ответ
        return new ReminderResponse(reminderRepository.save(reminder).getId(),
                                                            reminder.getTitle(),
                                                            reminder.getDescription(),
                                                            reminder.getRemind(),
                                                            reminder.getUser().getId());
    }





}
