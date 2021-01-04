package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TitlesId implements Serializable {

  private Long empNo;
  private String title;
  private LocalDate fromDate;

}
