package com.example.aggridserversidespringbootexample.service;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.request.tablerequestfields.ColumnVO;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.example.aggridserversidespringbootexample.config.Constants.ENTITY_ALIAS;
import static com.example.aggridserversidespringbootexample.config.Constants.ENTITY_ID_ALIAS;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class TableRequestFlatRepository<T> extends BaseTableRequestRepository implements TableRepository<T> {

  @PersistenceContext
  private EntityManager em;

  public List<T> getEntities(TableRequest request) {
    var ids = getEntitiesIds(request);
    var hql = createSelectByIds(request, ids);
    log.debug("HQL QUERY:\n{}", hql);
    return em.createQuery(hql).getResultList();
  }

  public Number getCount(TableRequest request) {
    var selectEntityCount = createSelectEntityCount();
    var from = createSelectFrom();
    var joining = ""; // getEntityFetchJoins();
    var where = createWhere(request);
    var hql = selectEntityCount + from + joining + where;

    try {
      log.debug("HQL COUNT:\n{}", hql);
      return em.createQuery(hql, Number.class).getSingleResult();
    } catch (NoResultException e) {
      return 0;
    }
  }

  public List<Long> getEntitiesIds(TableRequest request) {
    var selectEntityId = createSelectEntityId(request);
    var from = createSelectFrom();
    var where = createWhere(request);
    var orderBy = createOrderBy(request);
    var hql = selectEntityId + from + where + orderBy;
    log.debug("HQL ENTITIES IDS:\n{}", hql);

    var startRow = request.getStartRow();
    var maxResult = request.getLimit();

    Function<Object, Long> mapToLong =
        (Object r) -> {
          if (r instanceof Long) {
            return (Long) r;
          }
          return (Long) ((Object[]) r)[0];
        };
    List<?> list =
        em.createQuery(hql).setFirstResult(startRow).setMaxResults(maxResult).getResultList();
    return list.stream().map(mapToLong).collect(toList());
  }


  private String createOrderBy(TableRequest request) {
    if (!request.hasSort()) {
      return "";
    }

    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    var sortModel = request.getSortModel();

    var sortParts = new ArrayList<String>();

    sortModel.forEach(
        (item) -> {
            var mappedColId = getMappedField(item.getColId());
            sortParts.add(mappedColId + ' ' + item.getSort());
        });

    if (!sortParts.isEmpty()) {
      return " order by " + Joiner.on(", ").join(sortParts);
    } else {
      return "";
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

  private String createSelectEntityId(TableRequest request) {
    if (request.hasSort()) {
      var sortFields =
          request.getSortModel().stream()
              .map(s -> getMappedField(s.getColId()))
              .collect(joining(", "));

      return "select distinct " + ENTITY_ID_ALIAS + ", " + sortFields;
    }
    return "select distinct " + ENTITY_ID_ALIAS;
  }

  private String createWhereIds(List<Long> ids) {
    var idsString = Joiner.on(",").join(ids);
    return format(" where %s in (%s) ", ENTITY_ID_ALIAS, idsString);
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

  private String createWhere(TableRequest request) {
    var filterModel = request.getFilterModel();
    var valueCols = request.getValueCols();

    var whereParts = new ArrayList<String>();

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

}
