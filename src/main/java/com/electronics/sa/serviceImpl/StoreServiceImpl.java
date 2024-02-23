package com.electronics.sa.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.electronics.sa.entity.Store;
import com.electronics.sa.exception.StoreNotFoundException;
import com.electronics.sa.exception.UserNotFoundException;
import com.electronics.sa.repository.SellerRepository;
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
	
	private SellerRepository sellerRepository;

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> createStore(StoreRequest storeRequest) 
	{
		Store store = storeRepository.save(mapToStore(storeRequest));
		sellerRepository.findById(storeRequest.getSellerId()).map(seller -> {
			seller.setStore(store);
			return sellerRepository.save(seller);
			
		}).orElseThrow(() -> new UserNotFoundException("Seller not found for the store"));
		
		return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "Store details created successfully", mapToStoreResponse(store));
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(StoreRequest storeRequest, int storeId) 
	{
		return storeRepository.findById(storeId).map(store -> {
			
			Store updatedStore = mapToStore(storeRequest);
			updatedStore.setStoreId(store.getStoreId());
			updatedStore = storeRepository.save(updatedStore);
			return ResponseEntityProxy.getResponseEntity(HttpStatus.OK, "Store details updated successfully", mapToStoreResponse(updatedStore));
			
		}).orElseThrow(() -> new StoreNotFoundException("Store with the given Id not found, please provide a valid store Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreById(int storeId)
	{
		return storeRepository.findById(storeId).map(store -> {
			
			return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Store details found successfully", mapToStoreResponse(store));
			
		}).orElseThrow(() -> new StoreNotFoundException("Store with the given Id not found, please provide a valid store Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreBySeller(int sellerId)
	{
		return sellerRepository.findById(sellerId).map(seller -> {
			Store store = seller.getStore();
			if(store != null)
				return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Store details found successfully", mapToStoreResponse(store));
			else
			  throw new StoreNotFoundException("Store not found for the given seller Id");
			
		}).orElseThrow(() -> new UserNotFoundException("Seller with the given Id not found, please provide a valid seller Id"));
	}
	
	//Mapper Methods
	private StoreResponse mapToStoreResponse(Store store)
	{
		return StoreResponse.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.logoLink(store.getLogoLink())
				.about(store.getAbout())
				.build();
	}

	private Store mapToStore(StoreRequest storeRequest) 
	{
		return Store.builder()
				.storeName(storeRequest.getStoreName())
				.logoLink(storeRequest.getLogoLink())
				.about(storeRequest.getAbout())
				.build();
	}


}
