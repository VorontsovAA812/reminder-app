package ru.reminder.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.reminder.app.model.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Page<Reminder> findByUserId(Long userId, Pageable pageable);
}
