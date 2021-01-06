package com.example.aggridserversidespringbootexample.service;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ColumnVO;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.example.aggridserversidespringbootexample.util.FieldMapper.getMappedField;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class TableRequestGroupRepository<T> extends BaseTableRequestRepository implements TableRepository<Map> {

  @PersistenceContext
  private EntityManager em;

  public Number getCount(TableRequest request) {
    var selectGroupCount = createSelectGroupCount(request);
    var from = createSelectFrom();
    var where = createWhere(request);
    var groupBy = createGroupBy(request);
    var having = createHaving(request);
    var hql = selectGroupCount + from + where + groupBy + having;

    try {
      log.debug("HQL COUNT:\n{}", hql);
      return em.createQuery(hql, Number.class).getSingleResult();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public List<Map> getEntities(TableRequest request) {
    var selectGroup = createSelectGroup(request);
    var from = createSelectFrom();
    var where = createWhere(request);
    var groupBy = createGroupBy(request);
    var orderBy = createOrderBy(request);
    var having = createHaving(request);
    var hql = selectGroup + from + where + groupBy + orderBy + having;
    log.debug("HQL GROUP ENTITIES:\n{}", hql);

    var startRow = request.getStartRow();
    var maxResult = request.getLimit();
    return createQueryMap(hql).setFirstResult(startRow).setMaxResults(maxResult).getResultList();
  }

  private String createWhere(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var filterModel = request.getFilterModel();
    var valueCols = request.getValueCols();

    var whereParts = new ArrayList<String>();

    if (!groupKeys.isEmpty()) {
      IntStream.range(0, groupKeys.size())
          .forEach(
              (index) -> {
                var key = groupKeys.get(index);
                var colNameOrig = rowGroupCols.get(index).getField();
                var colName = getMappedField(colNameOrig);
                whereParts.add(colName + " = '" + key + "'");
              });
    }

    if (!filterModel.isEmpty()) {
      var keySet = filterModel.keySet();
      keySet.forEach(
          (key) -> {
            var item = filterModel.get(key);
            var isAggFunc =
                valueCols.stream().map(ColumnVO::getField).anyMatch((f) -> f.equals(key));
            if (!isAggFunc) {
              var keyMapped = getMappedField(key);
              whereParts.add(createFilter(keyMapped, item));
            }
          });
    }

    if (!whereParts.isEmpty()) {
      return " where " + Joiner.on(" and ").join(whereParts);
    } else {
      return "";
    }
  }

  private String createSelectGroupCount(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var valueCols = request.getValueCols();
    var groupKeys = request.getGroupKeys();
    var colsToSelect = new ArrayList<String>();

    var rowGroupCol = rowGroupCols.get(groupKeys.size());
    var mappedField = getMappedField(rowGroupCol.getField());
    colsToSelect.add(mappedField + " as " + rowGroupCol.getField());

    valueCols.forEach(
        (valueCol) ->
            colsToSelect.add(
                valueCol.getAggFunc()
                    + "("
                    + getMappedField(valueCol.getField())
                    + ") as "
                    + valueCol.getField()));

    return "select count( new map(" + Joiner.on(", ").join(colsToSelect) + ") ) ";
  }
  private TypedQuery<Map> createQueryMap(String query) {
    log.debug("QUERY:\n{}", query);
    return em.createQuery(query, Map.class);
  }

  protected String createGroupBy(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var colsToGroupBy = new ArrayList<String>();

    var rowGroupCol = rowGroupCols.get(groupKeys.size());
    var field = rowGroupCol.getField();
    colsToGroupBy.add(field);

    return " group by " + Joiner.on(", ").join(colsToGroupBy);
  }

  private String createHaving(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var filterModel = request.getFilterModel();
    var valueCols = request.getValueCols();

    var whereParts = new ArrayList<String>();

    //    if (!groupKeys.isEmpty()) {
    //      IntStream.range(0, groupKeys.size())
    //          .forEach(
    //              (index) -> {
    //                var key = groupKeys.get(index);
    //                var colName = rowGroupCols.get(index).getField();
    //                whereParts.add(colName + " = '" + key + "'");
    //              });
    //    }

    if (!filterModel.isEmpty()) {
      var keySet = filterModel.keySet();
      keySet.forEach(
          (key) -> {
            var item = filterModel.get(key);
            var aggFunc =
                valueCols.stream()
                    .filter(v -> v.getField().equals(key))
                    .map(ColumnVO::getAggFunc)
                    .findFirst();
            if (aggFunc.isPresent()) {
              var keyMapped = getMappedField(key);
              var keyAgg = format("%s ( %s )", aggFunc.get(), keyMapped);
              whereParts.add(createFilter(keyAgg, item));
            }
          });
    }

    if (!whereParts.isEmpty()) {
      return " having " + Joiner.on(" and ").join(whereParts);
    } else {
      return "";
    }
  }

  private String createSelectGroup(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var valueCols = request.getValueCols();
    var groupKeys = request.getGroupKeys();
    var colsToSelect = new ArrayList<String>();

    var rowGroupCol = rowGroupCols.get(groupKeys.size());
    var mappedField = getMappedField(rowGroupCol.getField());
    colsToSelect.add(mappedField + " as " + rowGroupCol.getField());

    valueCols.forEach(
        (valueCol) ->
            colsToSelect.add(
                valueCol.getAggFunc()
                    + "("
                    + getMappedField(valueCol.getField())
                    + ") as "
                    + valueCol.getField()));

    return "select new map(" + Joiner.on(", ").join(colsToSelect) + ") ";
  }

  private String createOrderBy(TableRequest request) {
    if (!request.hasSort()) {
      return "";
    }

    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var sortModel = request.getSortModel();


    var sortParts = new ArrayList<String>();
    var groupColIds =
        rowGroupCols.stream().map(ColumnVO::getId).limit(groupKeys.size() + 1).collect(toList());

    sortModel.forEach(
        (item) -> {
          if (groupColIds.contains(item.getColId())) {
            var mappedColId = getMappedField(item.getColId());
            sortParts.add(mappedColId + ' ' + item.getSort());
          }
        });

    if (!sortParts.isEmpty()) {
      return " order by " + Joiner.on(", ").join(sortParts);
    } else {
      return "";
    }
  }

}
