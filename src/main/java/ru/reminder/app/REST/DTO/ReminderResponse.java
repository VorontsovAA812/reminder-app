package ru.reminder.app.REST.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ReminderResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime remind;
    private Long userId;

}
