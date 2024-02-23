package com.electronics.sa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.electronics.sa.request_dto.AddressRequest;
import com.electronics.sa.response_dto.AddressResponse;
import com.electronics.sa.service.AddressService;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AddressController {
	
	private AddressService addressService;

	@PostMapping("/addresses")
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddress(@RequestBody AddressRequest addressRequest, @RequestParam(required = false ) int storeId)
	{
		return addressService.addAddress(addressRequest, storeId);
	}
	
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable int addressId)
	{
		return addressService.updateAddress(addressRequest, addressId);
	}
	
	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(@PathVariable int addressId)
	{
		return addressService.findAddressById(addressId);
	}
	
	@GetMapping("/stores/{storeId}/addresses")
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(@PathVariable int storeId)
	{
		return addressService.findAddressByStore(storeId);
	}
}
