package com.example.aggridserversidespringbootexample.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/** Ответ на запрос данных для таблиц и карты */
@Data
@AllArgsConstructor
public class DataResponse {

  private List<?> rows;
  private Number lastRow;

  public static DataResponse empty() {
    return new DataResponse(Collections.emptyList(), 0);
  }

  public static DataResponse fromListAndLastRow(List<?> data, Number lastRow) {
    if (isNull(lastRow)) {
      lastRow = data.size();
    }
    return new DataResponse(data, lastRow);
  }
}
