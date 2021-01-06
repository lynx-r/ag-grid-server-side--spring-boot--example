package com.example.aggridserversidespringbootexample.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.example.aggridserversidespringbootexample.config.Constants.ENTITY_ALIAS;
import static com.example.aggridserversidespringbootexample.config.Constants.ENTITY_ALIAS_TRIM;
import static java.lang.String.format;

@Slf4j
public class BaseTableRequestRepository {

  protected String entityName;

  protected String getEntityName() {return entityName;}

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  protected String createSelectFrom() {
    var joining = getEntityJoins();
    return " from " + getEntityName() + ENTITY_ALIAS + joining;
  }


  protected String createFilter(String key, Map<String, Object> item) {
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

  protected String createNumberFilter(String key, Map<String, Object> item) {
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

  protected String createTextFilter(String key, Map<String, Object> item) {
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

  protected String createDateFilter(String key, Map<String, Object> item) {
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

  protected String getEntityFetchJoins() {
    switch (getEntityName()) {
      case "Employees":
        return format(" join fetch %s.salaries salaries ", ENTITY_ALIAS_TRIM);
      default:
        return "";
    }
  }

  protected String getEntityJoins() {
    switch (getEntityName()) {
      case "Employees":
        return format(" join %s.salaries salaries ", ENTITY_ALIAS_TRIM);
      default:
        return "";
    }
  }
}
