package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ColumnVO;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.example.aggridserversidespringbootexample.util.ColumnVOFieldMapped.getMappedField;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
abstract class BaseDao<T> {

  private static final String ENTITY_ID_ALIAS = " entity.id ";
  private static final String ENTITY_ALIAS = " entity ";
  private static final String ENTITY_ALIAS_TRIM = "entity";

  @PersistenceContext protected EntityManager em;

  private Class<T> clazz;

  public BaseDao(Class<T> clazz) {
    this.clazz = clazz;
  }

  public DataResponse findAllByRequest(TableRequest request) {
    var maxResult = request.getEndRow() - request.getStartRow() + 1;

    var from = createSelectFrom();
    var where = createWhere(request);
    var orderBy = createOrderBy(request);

    if (isDoingGrouping(request.getRowGroupCols(), request.getGroupKeys())) {
      var selectGroup = createSelectGroup(request);
      var groupBy = createGroupBy(request);
      var having = createHaving(request);
      var hql = selectGroup + from + where + groupBy + orderBy + having;

      var results =
          createQueryMap(hql)
              .setFirstResult(request.getStartRow())
              .setMaxResults(maxResult)
              .getResultList();
      var count = getRowCount(request, results);
      var resultsForPage = cutResultsToPageSize(request, results);
      return DataResponse.fromListAndLastRow(resultsForPage, count);
    }

    var selectEntityId = createSelectEntityId();
    var hql = selectEntityId + from + where + orderBy;

    var ids =
        createQueryIds(hql)
            .setFirstResult(request.getStartRow())
            .setMaxResults(maxResult)
            .getResultList();

    if (ids.isEmpty()) {
      return DataResponse.empty();
    }

    hql = createSelectByIds(request, ids);
    var results = createQueryEntities(hql).getResultList();

    var count = getRowCount(request, results);
    var resultsForPage = cutResultsToPageSize(request, results);

    return DataResponse.fromListAndLastRow(resultsForPage, count);
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
                valueCol.getAggFunc() + "(" + getMappedField(valueCol.getField()) + ") as " + valueCol.getField()));

    return "select new map(" + Joiner.on(", ").join(colsToSelect) + ") ";
  }

  private String createSelectEntityId() {
    return "select distinct " + ENTITY_ID_ALIAS;
  }

  private String createSelectFrom() {
    var joining = getEntityJoins();
    return "from " + getEntityName() + ENTITY_ALIAS + joining;
  }

  private String createSelectByIds(TableRequest request, List<Long> ids) {
    var orderBy = createOrderBy(request, ids);
    var whereIds = createWhereIds(ids);
    var joining = getEntityFetchJoins();

    return "select distinct "
        + ENTITY_ALIAS
        + " from "
        + getEntityName()
        + ENTITY_ALIAS
        + joining
        + whereIds
        + orderBy;
  }

  private String getEntityFetchJoins() {
    switch (getEntityName()) {
      case "Employees":
        return format(" join fetch %s.salaries salaries ", ENTITY_ALIAS_TRIM);
      default:
        return "";
    }
  }

  private String getEntityJoins() {
    switch (getEntityName()) {
      case "Employees":
        return format(" join %s.salaries salaries ", ENTITY_ALIAS_TRIM);
      default:
        return "";
    }
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

  private String createFilter(String keyOrig, Map<String, Object> item) {
    var key = getMappedField(keyOrig);
    var filterType = (String) item.get("filterType");
    switch (filterType) {
      case "text":
        return createTextFilter(key, item);
      case "number":
        return createNumberFilter(key, item);
      case "date":
        return createDateFilter(key, item);
      default:
        log.error("unkonwn filter type: " + filterType);
        return "";
    }
  }

  private String createNumberFilter(String key, Map<String, Object> item) {
    var type = (String) item.get("type");
    var filter = item.get("filter");
    var filterTo = item.get("filterTo");
    switch (type) {
      case "equals":
        return key + " = " + filter;
      case "notEqual":
        return key + " != " + filter;
      case "greaterThan":
        return key + " > " + filter;
      case "greaterThanOrEqual":
        return key + " >= " + filter;
      case "lessThan":
        return key + " < " + filter;
      case "lessThanOrEqual":
        return key + " <= " + filter;
      case "inRange":
        return "(" + key + " >= " + filter + " and " + key + " <= " + filterTo + ")";
      default:
        log.error("unknown number filter type: " + type);
        return "true";
    }
  }

  private String createTextFilter(String key, Map<String, Object> item) {
    var type = (String) item.get("type");
    var filter = item.get("filter");
    switch (type) {
      case "equals":
        return key + " = '" + filter + "'";
      case "notEqual":
        return key + " != '" + filter + "'";
      case "contains":
        return key + " like '%" + filter + "%'";
      case "notContains":
        return key + " not like '%" + filter + "%'";
      case "startsWith":
        return key + " like '" + filter + "%'";
      case "endsWith":
        return key + " like '%" + filter + "'";
      default:
        log.error("unknown text filter type: " + type);
        return "true";
    }
  }

  private String createDateFilter(String key, Map<String, Object> item) {
    var type = (String) item.get("type");
    var dateFrom = item.get("dateFrom");
    var dateTo = item.get("dateTo");
    switch (type) {
      case "equals":
        return key + " = '" + dateFrom + "'";
      case "notEqual":
        return key + " != '" + dateFrom + "'";
      case "greaterThan":
        return key + " > '" + dateFrom + "'";
      case "lessThan":
        return key + " < '" + dateFrom + "'";
      case "inRange":
        return "(" + key + " between '" + dateFrom + "' and '" + dateTo + "')";
      default:
        log.error("unknown date filter type: " + type);
        return "true";
    }
  }

  private String createOrderBy(TableRequest request, List<Long> ids) {
    if (!request.hasSort()) {
      return "";
    }

    var caseBody =
        IntStream.range(0, ids.size())
            .mapToObj((i) -> format(" when %s then %s ", ids.get(i), i))
            .collect(joining());
    return format(" order by (case %s %s end) ", ENTITY_ID_ALIAS, caseBody);
  }

  private String createOrderBy(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var sortModel = request.getSortModel();

    var grouping = this.isDoingGrouping(rowGroupCols, groupKeys);

    var sortParts = new ArrayList<String>();
    if (!sortModel.isEmpty()) {

      var groupColIds =
          rowGroupCols.stream().map(ColumnVO::getId).limit(groupKeys.size() + 1).collect(toList());

      sortModel.forEach(
          (item) -> {
            if (!grouping || groupColIds.contains(item.getColId())) {
              sortParts.add(item.getColId() + ' ' + item.getSort());
            }
          });
    }

    if (!sortParts.isEmpty()) {
      return " order by " + Joiner.on(", ").join(sortParts);
    } else {
      return "";
    }
  }

  private String createGroupBy(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();

    if (this.isDoingGrouping(rowGroupCols, groupKeys)) {
      var colsToGroupBy = new ArrayList<String>();

      var rowGroupCol = rowGroupCols.get(groupKeys.size());
      var field = rowGroupCol.getField();
      colsToGroupBy.add(field);

      return " group by " + Joiner.on(", ").join(colsToGroupBy);
    } else {
      // select all columns
      return "";
    }
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

  private String createWhereIds(List<Long> ids) {
    var idsString = Joiner.on(",").join(ids);
    return format(" where %s in (%s) ", ENTITY_ID_ALIAS, idsString);
  }

  private <U> Number getRowCount(TableRequest request, List<U> data) {
    if (CollectionUtils.isEmpty(data)) {
      return null;
    }

    var currentLastRow = request.getStartRow() + data.size();
    return currentLastRow <= request.getEndRow() ? currentLastRow : -1;
  }

  private <U> List<U> cutResultsToPageSize(TableRequest request, List<U> results) {
    var pageSize = request.getEndRow() - request.getStartRow();
    if (nonNull(results) && results.size() > pageSize) {
      return results.subList(0, pageSize);
    } else {
      return results;
    }
  }

  private boolean isDoingGrouping(List<ColumnVO> rowGroupCols, List<String> groupKeys) {
    // we are not doing grouping if at the lowest level. we are at the lowest level
    // if we are grouping by more columns than we have keys for (that means the user
    // has not expanded a lowest level group, OR we are not grouping at all).
    return rowGroupCols.size() > groupKeys.size();
  }

  private TypedQuery<?> createQueryMap(String query) {
    log.debug("QUERY:\n{}", query);
    return em.createQuery(query, Map.class);
  }

  private TypedQuery<T> createQueryEntities(String query) {
    log.debug("HQL QUERY:\n{}", query);
    return em.createQuery(query, clazz);
  }

  private TypedQuery<Long> createQueryIds(String query) {
    log.debug("HQL QUERY:\n{}", query);
    return em.createQuery(query, Long.class);
  }

  private String getEntityName() {
    return clazz.getSimpleName();
  }
}
