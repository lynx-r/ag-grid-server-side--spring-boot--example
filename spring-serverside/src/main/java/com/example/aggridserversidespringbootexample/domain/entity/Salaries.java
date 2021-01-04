package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.*;

import java.time.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(SalariesId.class)
public class Salaries {

  @Id
  private Long empNo;

  @Id
  private LocalDate fromDate;

  private Integer salary;
  private LocalDate toDate;

}
