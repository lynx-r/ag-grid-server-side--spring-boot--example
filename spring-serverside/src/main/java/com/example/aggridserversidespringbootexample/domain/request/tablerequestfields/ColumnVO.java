package com.example.aggridserversidespringbootexample.domain.request.tablerequestfields;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Data
@NoArgsConstructor
public class ColumnVO {
  private String id;
  private String displayName;
  private String field;
  private String aggFunc;
}
