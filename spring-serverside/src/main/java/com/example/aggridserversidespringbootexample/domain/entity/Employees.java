package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.*;

import java.time.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Employees {

  @Id
  private Long empNo;
  private LocalDate birthDate;
  private String firstName;
  private String lastName;
  private String gender;
  private LocalDate hireDate;

}
