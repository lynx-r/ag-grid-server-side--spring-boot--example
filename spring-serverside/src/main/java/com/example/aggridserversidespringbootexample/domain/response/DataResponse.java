package com.example.aggridserversidespringbootexample.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Ответ на запрос данных для таблиц и карты */
@Data
@AllArgsConstructor
public class DataResponse<T> {

  private Set<T> data;

//  // todo ??
//  private List<String> secondaryColumns;
//  // todo ??
//  private Number lastRow;

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
