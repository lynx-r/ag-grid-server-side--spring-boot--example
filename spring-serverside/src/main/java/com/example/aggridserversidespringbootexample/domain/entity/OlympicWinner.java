package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class OlympicWinner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String athlete;
  private Integer age;
  private String country;
  private String countryGroup;
  private Integer year;
  private String date;
  private String sport;
  private Integer gold;
  private Integer silver;
  private Integer bronze;
  private Integer total;


}
