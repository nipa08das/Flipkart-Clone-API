package com.electronics.sa.service;

import org.springframework.http.ResponseEntity;

import com.electronics.sa.request_dto.AddressRequest;
import com.electronics.sa.response_dto.AddressResponse;
import com.electronics.sa.util.ResponseStructure;

public interface AddressService {

	ResponseEntity<ResponseStructure<AddressResponse>> addAddress(AddressRequest addressRequest, int storeId);

	ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(AddressRequest addressRequest, int addressId);

	ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(int addressId);

	ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(int storeId);

}
