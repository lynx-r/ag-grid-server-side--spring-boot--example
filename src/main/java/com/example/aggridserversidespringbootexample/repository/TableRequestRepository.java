package com.example.aggridserversidespringbootexample.repository;

import com.example.aggridserversidespringbootexample.domain.request.TableRequest;
import com.example.aggridserversidespringbootexample.domain.response.DataResponse;

import java.util.Optional;

public interface TableRequestRepository<E> {

    DataResponse<E> findAllByRequest(TableRequest request);

}
