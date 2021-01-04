package com.example.aggridserversidespringbootexample.domain.entity;

import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

  public String getSalariesAsString() {
    return Joiner.on(", ")
        .join(salaries.stream().map(Salaries::getSalary).map(Object::toString).collect(toList()));
  }

  public Integer getTotalSalary() {
    return salaries.stream().mapToInt(Salaries::getSalary).sum();
  }
}
