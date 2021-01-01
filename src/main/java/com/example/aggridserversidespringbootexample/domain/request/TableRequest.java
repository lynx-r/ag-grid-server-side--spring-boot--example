package com.example.aggridserversidespringbootexample.domain.request;

import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.RowGroup;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.SortModel;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ValueCol;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TableRequest {

  private int startRow;
  private int endRow;

  private Boolean pivotMode;
  private List<String> pivotCols;
  private List<String> groupKeys;
  private List<RowGroup> rowGroupCols;
  private List<SortModel> sortModel;
  private Map<String, Map<String, Object>> filterModel;
  private List<ValueCol> valueCols;
  private Number resultCount;

  public boolean isPaged() {
    return startRow >= 0 && endRow > 0;
  }

  public boolean hasFilter() {
    return !CollectionUtils.isEmpty(filterModel);
  }

  public boolean hasSort() {
    return !CollectionUtils.isEmpty(sortModel);
  }
}
