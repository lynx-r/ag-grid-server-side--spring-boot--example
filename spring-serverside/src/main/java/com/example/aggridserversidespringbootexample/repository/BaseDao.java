package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
import com.example.aggridserversidespringbootexample.service.TableRequestFlatRepository;
import com.example.aggridserversidespringbootexample.service.TableRequestGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Slf4j
@RequiredArgsConstructor
abstract class BaseDao<T> {

  private Class<T> clazz;

  @Autowired
  private ApplicationContext applicationContext;

  public BaseDao(Class<T> clazz) {
    this.clazz = clazz;
  }

  public DataResponse findAllByRequest(TableRequest request) {
    if (isDoingGrouping(request)) {
      return findAllGrouped(request);
    }
    return findAll(request);
  }

  private DataResponse findAll(TableRequest request) {
    var repo
        = applicationContext.getBean(TableRequestFlatRepository.class);
    repo.setEntityName(getEntityName());

    var count = repo.getCount(request);
    if (count.intValue() == 0) {
      return DataResponse.empty();
    }

    var results = repo.getEntities(request);
    return DataResponse.fromListAndCount(results, count);
  }

  private DataResponse findAllGrouped(TableRequest request) {
    var repo
        = applicationContext.getBean(TableRequestGroupRepository.class);
    repo.setEntityName(getEntityName());

    Number count = repo.getCount(request);
    if (count.intValue() == 0) {
      return DataResponse.empty();
    }

    var results = repo.getEntities(request);
    return DataResponse.fromListAndCount(results, count);
  }

  private boolean isDoingGrouping(TableRequest request) {
    // if we are grouping by more columns than we have keys for (that means the user
    // has not expanded a lowest level group, OR we are not grouping at all).
    var rowGroupCols = request.getRowGroupCols();
    var groupKeys = request.getGroupKeys();
    return rowGroupCols.size() > groupKeys.size();
  }

  private String getEntityName() {
    return clazz.getSimpleName();
  }
}
