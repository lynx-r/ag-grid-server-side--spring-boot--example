package com.example.aggridserversidespringbootexample.domain.request;

import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ColumnVO;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.SortModel;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Data
public class TableRequest {

  private int startRow, endRow;

  // row group columns
  private List<ColumnVO> rowGroupCols;

  // value columns
  private List<ColumnVO> valueCols;

  // pivot columns
  private List<String> pivotCols;

  // true if pivot mode is one, otherwise false
  private boolean pivotMode;

  // what groups the user is viewing
  private List<String> groupKeys;

  // if filtering, what the filter model is
  private Map<String, Map<String, Object>> filterModel;

  // if sorting, what the sort model is
  private List<SortModel> sortModel;

  public Integer getLimit() {
    return endRow - startRow + 1;
  }

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
