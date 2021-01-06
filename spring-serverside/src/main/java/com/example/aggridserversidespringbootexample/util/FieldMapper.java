package com.example.aggridserversidespringbootexample.util;

import java.util.Map;

import static com.example.aggridserversidespringbootexample.config.Constants.ENTITY_ALIAS_TRIM;

public class FieldMapper {

  public static String getMappedField(String field) {
    var map =
        Map.of(
            "totalSalary", "sum(salaries.salary)",
            "salariesAsString", "salaries.salary",
            "salariesFromDate", "salaries.fromDate");
    if (map.containsKey(field)) {
      return map.get(field);
    }
    return ENTITY_ALIAS_TRIM + "." + field;
  }
}
