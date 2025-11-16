package ru.reminder.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.reminder.app.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long>
{
}
