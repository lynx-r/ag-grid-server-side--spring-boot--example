package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptManagerId implements Serializable {

  private Long empNo;
  private String deptNo;

}
