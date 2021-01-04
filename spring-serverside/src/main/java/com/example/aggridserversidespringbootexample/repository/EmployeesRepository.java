package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.entity.Employees;
import org.springframework.stereotype.Component;

@Component
public class EmployeesRepository extends BaseDao<Employees> implements TableRequestRepository {

  public EmployeesRepository() {
    super(Employees.class);
  }
}
