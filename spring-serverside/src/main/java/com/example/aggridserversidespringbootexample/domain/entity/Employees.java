package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Employees {

  @Id private Long empNo;
  private LocalDate birthDate;
  private String firstName;
  private String lastName;
  private String gender;
  private LocalDate hireDate;

  @OneToMany
  @JoinColumn(name = "emp_no")
  private List<Salaries> salaries;

  public Integer getTotalSalary() {
    return salaries.stream().mapToInt(Salaries::getSalary).sum();
  }

}
