package ru.reminder.app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.reminder.app.bot.TelegramBot;
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
    private final EmailService emailService;

    private static final String TELEGRAM_FORMAT=  "%s\n%s";
    private static final String EMAIL_FORMAT=  "%s";



    @Scheduled(cron = "${bot.cron}")
    public void sendScheduledReminders() {

        List<Reminder> dueReminders = reminderRepo.findUnnotifiedReminders(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusHours(3));

        if (!dueReminders.isEmpty()) {
            for (Reminder reminder : dueReminders) {
                // String tgText = String.format(TELEGRAM_FORMAT, reminder.getTitle(), reminder.getDescription());
                String emailText = String.format(EMAIL_FORMAT, reminder.getDescription());
                String userEmail = reminder.getUser().getEmail();

                // try {
                //    telegramBot.sendMessage(reminder.getUser().getChatId(), tgText);
                //} catch (Exception e) {
                //      log.error("Failed to send Telegram for reminder ID {}: {}", reminder.getId(), e.getMessage());
                //
                if (userEmail != null) {
                    try {
                        emailService.sendEmail(userEmail, reminder.getTitle(), emailText);
                    } catch (Exception e) {
                        log.error("Failed to send Email for reminder ID {}: {}", reminder.getId(), e.getMessage());
                    }
                }
                reminder.setNotified(true);
                reminderRepo.saveAndFlush(reminder);


                }
            }
        }
    }
