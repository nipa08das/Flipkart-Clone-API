package com.electronics.sa.service;

import org.springframework.http.ResponseEntity;

import com.electronics.sa.request_dto.StoreRequest;
import com.electronics.sa.response_dto.StoreResponse;
import com.electronics.sa.util.ResponseStructure;

public interface StoreService {

	ResponseEntity<ResponseStructure<StoreResponse>> createStore(StoreRequest storeRequest);

}
