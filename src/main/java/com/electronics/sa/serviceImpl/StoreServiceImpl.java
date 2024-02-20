package com.electronics.sa.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.electronics.sa.entity.Store;
import com.electronics.sa.repository.StoreRepository;
import com.electronics.sa.request_dto.StoreRequest;
import com.electronics.sa.response_dto.StoreResponse;
import com.electronics.sa.service.StoreService;
import com.electronics.sa.util.ResponseEntityProxy;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StoreServiceImpl implements StoreService{
	
	private StoreRepository storeRepository;

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> createStore(StoreRequest storeRequest) 
	{
		Store store = storeRepository.save(mapToStore(storeRequest));
		
		return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "Store details created successfully", mapToStoreResponse(store));
	}

	private StoreResponse mapToStoreResponse(Store store)
	{
		return StoreResponse.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.about(store.getAbout())
				.build();
	}

	private Store mapToStore(StoreRequest storeRequest) 
	{
		return Store.builder()
				.storeName(storeRequest.getStoreName())
				.about(storeRequest.getAbout())
				.build();
	}

}
