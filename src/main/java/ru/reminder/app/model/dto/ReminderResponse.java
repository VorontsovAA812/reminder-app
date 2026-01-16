package ru.reminder.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
public class ReminderResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime remind;
    private Long userId;

}
