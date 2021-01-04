package com.example.aggridserversidespringbootexample.util;

import java.util.Map;

public class FieldMapper {

  public static String getMappedField(String field) {
    var map = Map.of(
        "totalSalary",  "sum(salaries.salary)",
        "salariesAsString", "salaries.salary",
        "salariesFromDate", "salaries.fromDate"
    );
    if (map.containsKey(field)) {
      return map.get(field);
    }
    return field;
  }

}
