package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.*;

import java.time.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(DeptManagerId.class)
public class DeptManager {

  @Id
  private Long empNo;

  @Id
  private String deptNo;

  private LocalDate fromDate;
  private LocalDate toDate;

}
