package ru.reminder.app.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class PagingResult<T> {

    private Collection<T> content;
    private Integer total;
    private Integer current;

    public PagingResult(Collection<T> content, Integer totalPages, Integer page) {
        this.content = content;
        total = totalPages;
        current = page + 1;
    }
}