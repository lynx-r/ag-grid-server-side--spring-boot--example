package com.example.aggridserversidespringbootexample.domain.request.tablerequestfields;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@NoArgsConstructor
@Data
public class RowGroup {
  private String id;
  private String field;
  private List<Statement> whereStatements;
  private String displayName;

  public RowGroup(String id, String field, List<Statement> whereStatements, String displayName) {
    this.id = id;
    this.field = field;
    this.whereStatements = whereStatements != null ? whereStatements : new ArrayList<>();
    this.displayName = displayName;
  }

  public boolean hasWhere() {
    return whereStatements != null && !whereStatements.isEmpty();
  }

  public String getWhereField() {
    if (!hasWhere()) {
      return "";
    }
    return whereStatements
        .stream()
        .map(st -> format("%s %s %s", field, st.getOp(), st.getValue()))
        .collect(joining(" AND "));
  }
}
