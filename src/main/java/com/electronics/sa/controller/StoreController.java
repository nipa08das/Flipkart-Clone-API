package com.electronics.sa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electronics.sa.request_dto.StoreRequest;
import com.electronics.sa.response_dto.StoreResponse;
import com.electronics.sa.service.StoreService;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class StoreController {
	
	private StoreService storeService;

	@PostMapping("/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> createStore(@RequestBody StoreRequest storeRequest)
	{
		return storeService.createStore(storeRequest);
	}
	
	@PutMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(@RequestBody StoreRequest storeRequest, @PathVariable int storeId)
	{
		return storeService.updateStore(storeRequest, storeId);
	}
	
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreById(@PathVariable int storeId)
	{
		return storeService.findStoreById(storeId);
	}
	
	@GetMapping("/sellers/{sellerId}/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreBySeller(@PathVariable int sellerId)
	{
		return storeService.findStoreBySeller(sellerId);
	}
	
}
