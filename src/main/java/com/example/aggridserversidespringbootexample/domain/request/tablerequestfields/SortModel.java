package com.example.aggridserversidespringbootexample.domain.request.tablerequestfields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SortModel {
  private String colId;
  private String sort;
}
