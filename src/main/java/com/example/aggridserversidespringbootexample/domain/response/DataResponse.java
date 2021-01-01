package com.example.aggridserversidespringbootexample.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

/**
 * Ответ на запрос данных для таблиц и карты
 */
@Data
@AllArgsConstructor
public class DataResponse<T> {

    private Set<T> data;
    private Number count;

    public static <T> DataResponse<T> empty() {
        return new DataResponse<>(Collections.emptySet(), 0);
    }

    public static <T> DataResponse<T> fromListAndTotalCount(List<T> data, Number totalCount) {
        if (totalCount == null) {
            totalCount = data.size();
        }
        return new DataResponse<>(new LinkedHashSet<>(data), totalCount);
    }

}
