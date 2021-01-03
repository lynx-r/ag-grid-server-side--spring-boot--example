package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.config.TemplateParser;
import com.example.aggridserversidespringbootexample.domain.enums.EnumTemplateType;
import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ColumnVO;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
import com.example.aggridserversidespringbootexample.service.RequestMapperService;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
abstract class BaseDao<T> {

  @PersistenceContext protected EntityManager em;

  @Autowired private RequestMapperService requestMapperService;
  @Autowired private TemplateParser templateParser;

  private Class<T> clazz;

  public BaseDao(Class<T> clazz) {
    this.clazz = clazz;
  }

  //  public DataResponse<T> findAllByRequest(TableRequest request) {
  //    TypedQuery<T> query;
  //    var isRequestForAll = !request.hasFilter() && !request.isPaged();
  //    String hql;
  //    if (isRequestForAll) {
  //      hql = createHql(request, EnumTemplateType.FIND_ALL);
  //    } else {
  //      hql = createHql(request, EnumTemplateType.FIND_ALL_IDS);
  //      List<?> idsRaw;
  //      if (request.isPaged()) {
  //        var maxResult = request.getEndRow() - request.getStartRow() + 1;
  //        idsRaw =
  //            createQuery(hql)
  //                .setFirstResult(request.getStartRow())
  //                .setMaxResults(maxResult)
  //                .getResultList();
  //      } else {
  //        idsRaw = createQuery(hql).getResultList();
  //      }
  //
  //      var ids = idsRaw.stream().map(this::idToString).collect(toList());
  //      var orderBy = createOrderBy(request, ids);
  //      var whereIds = createWhereIds(ids);
  //
  //      hql = createHql(request, EnumTemplateType.FIND_ALL, whereIds, orderBy);
  //    }
  //    query = createQueryEntities(hql);
  //
  //    var results = query.getResultList();
  //    var count = getRowCount(request, results);
  //    var resultsForPage = cutResultsToPageSize(request, results);
  //
  //    return DataResponse.fromListAndTotalCount(resultsForPage, count);
  //  }

  public DataResponse findAllByRequest(TableRequest request) {
    var maxResult = request.getEndRow() - request.getStartRow() + 1;

    var from = createSelectFrom();
    var where = createWhere(request);
    var orderBy = createOrderBy(request);
    var groupBy = createGroupBy(request);

    if (isDoingGrouping(request.getRowGroupCols(), request.getGroupKeys())) {
      var selectGroup = createSelectGroup(request);
      var hql = selectGroup + from + where + groupBy + orderBy;

      var results = createQueryMap(hql)
              .setFirstResult(request.getStartRow())
              .setMaxResults(maxResult)
              .getResultList();
      var count = getRowCount(request, results);
      var resultsForPage = cutResultsToPageSize(request, results);
      return DataResponse.fromListAndLastRow(resultsForPage, count);
    }

    var selectEntityId = createSelectEntityId();
    var hql = selectEntityId + from + where + groupBy + orderBy;

    var ids = createQueryIds(hql)
        .setFirstResult(request.getStartRow())
        .setMaxResults(maxResult)
        .getResultList();

    if (ids.isEmpty()) {
      return DataResponse.empty();
    }

    hql = createQueryByIds(request, ids);
    var results = createQueryEntities(hql).getResultList();

    var count = getRowCount(request, results);
    var resultsForPage = cutResultsToPageSize(request, results);

    return DataResponse.fromListAndLastRow(resultsForPage, count);
  }

  private String createQueryByIds(TableRequest request, List<Long> ids) {
    var orderBy = createOrderBy(request, ids);
    var whereIds = createWhereIds(ids);

    return "from " + getEntityName() + " entity " + whereIds + " " + orderBy;
  }

  private String createSelectGroup(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var valueCols = request.getValueCols();
    var groupKeys = request.getGroupKeys();
    var colsToSelect = new ArrayList<String>();

    var rowGroupCol = rowGroupCols.get(groupKeys.size());
    colsToSelect.add(rowGroupCol.getField() + " as " + rowGroupCol.getField());

    valueCols.forEach(
        (valueCol) ->
            colsToSelect.add(
                valueCol.getAggFunc()
                    + "("
                    + valueCol.getField()
                    + ") as "
                    + valueCol.getField()));

    return "select new map(" + Joiner.on(", ").join(colsToSelect) + ") ";
  }

  private String createSelectEntityId() {
    return "select entity.id ";
  }

  private String createSelectFrom() {
    return "from " + getEntityName() + " entity ";
  }

  private String createWhere(TableRequest request) {
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var filterModel = request.getFilterModel();

    var whereParts = new ArrayList<String>();

    if (!groupKeys.isEmpty()) {
      IntStream.range(0, groupKeys.size())
          .forEach(
              (index) -> {
                var key = groupKeys.get(index);
                var colName = rowGroupCols.get(index).getField();
                whereParts.add(colName + " = '" + key + "'");
              });
    }

    if (!filterModel.isEmpty()) {
      var keySet = filterModel.keySet();
      keySet.forEach(
          (key) -> {
            var item = filterModel.get(key);
            whereParts.add(createFilterHql(key, item));
          });
    }

    if (!whereParts.isEmpty()) {
      return " where " + Joiner.on(" and ").join(whereParts);
    } else {
      return "";
    }
  }

  private String createFilterHql(String key, Map<String, Object> item) {
    var filterType = (String) item.get("filterType");
    switch (filterType) {
      case "text":
        return createTextFilterHql(key, item);
      case "number":
        return createNumberFilterHql(key, item);
      default:
        log.error("unkonwn filter type: " + filterType);
        return "";
    }
  }

  private String createNumberFilterHql(String key, Map<String, Object> item) {
    String type = (String) item.get("type");
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

  private String createTextFilterHql(String key, Map<String, Object> item) {
    String type = (String) item.get("type");
    var filter = item.get("filter");
    switch (type) {
      case "equals":
        return key + " = \"" + filter + "\"";
      case "notEqual":
        return key + " != \"" + filter + "\"";
      case "contains":
        return key + " like \"%" + filter + "%\"";
      case "notContains":
        return key + " not like \"%" + filter + "%\"";
      case "startsWith":
        return key + " like \"" + filter + "%\"";
      case "endsWith":
        return key + " like \"%" + filter + "\"";
      default:
        log.error("unknown text filter type: " + type);
        return "true";
    }
  }

  private String createOrderBy(TableRequest request, List<Long> ids) {
    var orderBy = "";
    if (request.hasSort()) {
      var caseBody =
          IntStream.range(0, ids.size())
              .mapToObj((i) -> format(" when %s then %s ", ids.get(i), i))
              .collect(joining());
      orderBy += " order by (case entity.id " + caseBody + " end) ";
    }
    return orderBy;
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
      colsToGroupBy.add(rowGroupCol.getField());

      return " group by " + Joiner.on(", ").join(colsToGroupBy);
    } else {
      // select all columns
      return "";
    }
  }

  private String createWhereIds(List<Long> ids) {
    var idsString = Joiner.on(",").join(ids);
    return "where entity.id in (" + idsString + ")";
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

  private String idToString(Object o) {
    if (o instanceof Long) {
      return ((Long) o).toString();
    }
    return ((Object[]) o)[0].toString();
  }

  private String createHql(TableRequest request, EnumTemplateType templateQuery) {
    return createHql(request, templateQuery, "", "");
  }

  private String createHql(
      TableRequest request, EnumTemplateType templateQuery, String whereIds, String orderBy) {
    var params =
        Map.of(
            "request", request,
            "orderBy", orderBy,
            "whereIds", whereIds,
            "constants", requestMapperService.constants(),
            "entityClass", getEntityName(),
            "entityMapper", requestMapperService.entityMapper(getEntityName()),
            "filterTypeToHqlOperator", requestMapperService.filterTypeToHqlOperator());
    var templateName = getHqlTemplateName(templateQuery);
    return templateParser.prepareQuery(templateName, params);
  }

  private TypedQuery<?> createQueryMap(String hql) {
    return em.createQuery(hql, Map.class);
  }

  private Query createQuery(String query) {
    log.debug("QUERY:\n{}", query);
    return em.createQuery(query);
  }

  private TypedQuery<T> createQueryEntities(String query) {
    log.debug("HQL QUERY:\n{}", query);
    return em.createQuery(query, clazz);
  }

  private TypedQuery<Long> createQueryIds(String query) {
    log.debug("HQL QUERY:\n{}", query);
    return em.createQuery(query, Long.class);
  }

  private String getHqlTemplateName(EnumTemplateType templateQuery) {
    return format("hql/entity__%s__hql.ftl", templateQuery).toLowerCase();
  }

  private String getEntityName() {
    return clazz.getSimpleName();
  }
}
