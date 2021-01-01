package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.entity.OlympicWinner;
import org.springframework.stereotype.Component;

@Component
public class OlympicWinnerTableRequestRepositoryImpl extends BaseDao<OlympicWinner>
    implements OlympicWinnerTableRequestRepository {

  public OlympicWinnerTableRequestRepositoryImpl() {
    super(OlympicWinner.class);
  }
}
