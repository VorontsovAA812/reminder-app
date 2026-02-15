package ru.reminder.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.reminder.app.model.entity.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Page<Reminder> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT r FROM Reminder r " +
                "WHERE r.user.id = :userId " +
                    "AND r.remind BETWEEN :start AND :end")
    Page<Reminder> findRemindBetweenDates(@Param("userId") Long userId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     Pageable pageable);

    @Query("SELECT r FROM Reminder r " +
                "WHERE r.user.id = :userId AND r.remind = :remind")
    Optional<Reminder> findUserReminder(Long userId, LocalDateTime remind);

    @Query("SELECT r FROM Reminder r "+
                "JOIN r.user u " +
                    "WHERE u.chatId IS NOT NULL " +
                        "AND  r.notified = false " +
                        "AND r.remind <= :currentTime")
    List<Reminder> findUnnotifiedReminders(LocalDateTime currentTime);

   }