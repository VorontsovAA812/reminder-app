package ru.reminder.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingResult<T> {
    private Collection<T> content;
    private Integer total;
    private Integer current;
}