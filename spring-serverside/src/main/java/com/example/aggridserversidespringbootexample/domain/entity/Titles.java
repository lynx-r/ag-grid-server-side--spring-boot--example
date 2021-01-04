package com.example.aggridserversidespringbootexample.domain.entity;

import lombok.*;

import java.time.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(TitlesId.class)
public class Titles {

  @Id
  private Long empNo;

  @Id
  private String title;

  @Id
  private LocalDate fromDate;

  private LocalDate toDate;

}
