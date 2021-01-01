package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.entity.OlympicWinner;
import org.springframework.data.repository.CrudRepository;

public interface OlympicWinnerRepository
    extends CrudRepository<OlympicWinner, Long>, OlympicWinnerTableRequestRepository {}
