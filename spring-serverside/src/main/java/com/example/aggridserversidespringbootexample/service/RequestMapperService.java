package com.example.aggridserversidespringbootexample.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.aggridserversidespringbootexample.util.JsonUtils.getJsonOfMapOfMaps;

@Service
public class RequestMapperService {

  private Map<String, Map<String, Object>> requestMapperObject;

  @PostConstruct
  private void init() throws IOException {
    requestMapperObject = getJsonOfMapOfMaps("classpath:mapper/ag-grid--to--hql--field-mapper.json");
  }

  public Map<String, Object> constants() {
    return requestMapperObject.get("constants");
  }

  public Map<String, Object> filterTypeToHqlOperator() {
    return requestMapperObject.get("filterTypeToHqlOperator");
  }

  public Map<String, Object> filterTypeWithPercentSign() {
    return requestMapperObject.get("filterTypeWithPercentSign");
  }

  public Map<String, Object> entityMapper(String entityName) {
    return requestMapperObject.getOrDefault(entityName.toUpperCase(), Collections.emptyMap());
  }

}
