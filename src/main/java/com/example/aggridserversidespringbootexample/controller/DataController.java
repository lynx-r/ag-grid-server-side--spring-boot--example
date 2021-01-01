package com.example.aggridserversidespringbootexample.controller;

import com.example.aggridserversidespringbootexample.domain.entity.OlympicWinner;
import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;
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

  @PostMapping("olympic-winners")
  public DataResponse<OlympicWinner> getOlympicWinners(@RequestBody TableRequest tableRequest) {
    return olympicWinnerRepository.findAllByRequest(tableRequest);
  }
}
