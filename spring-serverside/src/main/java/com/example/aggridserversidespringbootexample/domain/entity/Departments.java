package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.*;

import java.time.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Departments {

  @Id
  private String deptNo;
  private String deptName;

}
