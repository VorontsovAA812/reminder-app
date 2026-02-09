package ru.reminder.app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.reminder.app.model.entity.Reminder;
import ru.reminder.app.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepo;
    private final TelegramBot telegramBot;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sendScheduledReminders() {

        List<Reminder> dueReminders = reminderRepo.findUnnotifiedReminders(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusHours(3));

        if (!dueReminders.isEmpty()) {
            for (Reminder reminder : dueReminders) {

                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("🔔 *").append(reminder.getTitle()).append("*\n\n");
                    sb.append(reminder.getDescription());

                    telegramBot.sendMessage(reminder.getUser().getChatId(), sb.toString());
                    reminder.setNotified(true);
                    reminderRepo.save(reminder);

                } catch (Exception e) {
                    log.error("Ошибка при отправке напоминания ID {}: {}", reminder.getId(), e.getMessage());
                }

            }
        }
    }
}
