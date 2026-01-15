package ru.reminder.app.query;

import org.springframework.data.domain.Sort;

public enum SortingOptions {


    NAME(Sort.by("title")),
    DATE(Sort.by("remind"));

    final Sort sort;

    SortingOptions(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }

}
