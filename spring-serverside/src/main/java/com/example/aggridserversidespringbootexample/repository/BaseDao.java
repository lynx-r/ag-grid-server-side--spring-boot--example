package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.config.TemplateParser;
import com.example.aggridserversidespringbootexample.domain.enums.EnumTemplateType;
import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
import com.example.aggridserversidespringbootexample.service.RequestMapperService;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.String.format;
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

  public DataResponse<T> findAllByRequest(TableRequest request) {
    Number count = null;
    TypedQuery<T> query;
    var isRequestForAll = !request.hasFilter() && !request.isPaged();
    String hql;
    if (isRequestForAll) {
      hql = createHql(request, EnumTemplateType.FIND_ALL);
    } else {
      count = countAllByRequest(request);
      if (count.intValue() == 0) {
        return DataResponse.empty();
      }

      hql = createHql(request, EnumTemplateType.FIND_ALL_IDS);
      List<?> idsRaw;
      if (request.isPaged()) {
        var maxResult = request.getEndRow() - request.getStartRow();
        idsRaw =
            createQuery(hql)
                .setFirstResult(request.getStartRow())
                .setMaxResults(maxResult)
                .getResultList();
      } else {
        idsRaw = createQuery(hql).getResultList();
      }

      var ids = idsRaw.stream().map(this::idToString).collect(toList());
      var orderBy = createOrderBy(request, ids);
      var whereIds = createWhereIds(ids);

      hql = createHql(request, EnumTemplateType.FIND_ALL, whereIds, orderBy);
    }
    query = createQueryEntities(hql);

    var data = query.getResultList();
    return DataResponse.fromListAndTotalCount(data, count);
  }

  private String createOrderBy(TableRequest request, List<String> ids) {
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

  private String createWhereIds(List<String> ids) {
    if (ids.isEmpty()) {
      return "";
    }

    var idsString = Joiner.on(",").join(ids);
    return "where entity.id in (" + idsString + ")";
  }

  private Number countAllByRequest(TableRequest request) {
    String query = createHql(request, EnumTemplateType.COUNT_ALL);
    try {
      return (Number) createQuery(query).getSingleResult();
    } catch (NoResultException e) {
      return 0;
    }
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

  private Query createQuery(String query) {
    log.debug("QUERY:\n{}", query);
    return em.createQuery(query);
  }

  private TypedQuery<T> createQueryEntities(String query) {
    log.debug("HQL QUERY:\n{}", query);
    return em.createQuery(query, clazz);
  }

  private String getHqlTemplateName(EnumTemplateType templateQuery) {
    return format("hql/entity__%s__hql.ftl", templateQuery).toLowerCase();
  }

  private String getEntityName() {
    return clazz.getSimpleName();
  }
}
