package ru.reminder.app.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;


@Getter
@RequiredArgsConstructor
public enum SortingOptions {
    NAME(Sort.by("title")),
    DATE(Sort.by("remind"));

    final Sort sort;
}
