package ru.reminder.app.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderDto {

    private String title;

    private String description;

    private LocalDateTime remind;

    private Long userId; // временно берём из запроса (пока не подключена ауентификация))


}
