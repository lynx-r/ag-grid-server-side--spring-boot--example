package com.example.aggridserversidespringbootexample.domain.request.tablerequestfields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueCol {
  private String id;
  private String aggFunc;
  private String displayName;
  private String field;
}
