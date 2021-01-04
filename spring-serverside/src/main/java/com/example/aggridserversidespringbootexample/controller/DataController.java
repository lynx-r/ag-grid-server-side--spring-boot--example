package com.example.aggridserversidespringbootexample.controller;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
import com.example.aggridserversidespringbootexample.repository.EmployeesRepository;
import com.example.aggridserversidespringbootexample.repository.OlympicWinnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class DataController {

  private final OlympicWinnerRepository olympicWinnerRepository;
  private final EmployeesRepository employeesRepository;

  @PostMapping("olympic-winners")
  public DataResponse getOlympicWinners(@RequestBody TableRequest tableRequest) {
    return olympicWinnerRepository.findAllByRequest(tableRequest);
  }

  @PostMapping("employees")
  public DataResponse getEmployees(@RequestBody TableRequest tableRequest) {
    return employeesRepository.findAllByRequest(tableRequest);
  }


}
