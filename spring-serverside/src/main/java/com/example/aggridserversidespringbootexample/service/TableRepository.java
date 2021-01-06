package com.example.aggridserversidespringbootexample.service;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;

import java.util.List;

public interface TableRepository<T> {
  void setEntityName(String entityName);
  Number getCount(TableRequest request);
  List<T> getEntities(TableRequest request);
}
