package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalariesId implements Serializable {

  @Column(name = "emp_no")
  private Long empNo;
  private LocalDate fromDate;

}
