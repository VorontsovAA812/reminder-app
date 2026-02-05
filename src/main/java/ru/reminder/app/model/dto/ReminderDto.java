package ru.reminder.app.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDto {
    private String title;
    private String description;
    private LocalDateTime remind;
    private Long userId; // временно берём из запроса (пока не подключена ауентификация))
}
