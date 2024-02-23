package com.electronics.sa.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.electronics.sa.entity.Address;
import com.electronics.sa.enums.AddressType;
import com.electronics.sa.exception.AddressNotFoundException;
import com.electronics.sa.exception.InvalidAddressTypeException;
import com.electronics.sa.exception.StoreNotFoundException;
import com.electronics.sa.repository.AddressRepository;
import com.electronics.sa.repository.StoreRepository;
import com.electronics.sa.request_dto.AddressRequest;
import com.electronics.sa.response_dto.AddressResponse;
import com.electronics.sa.service.AddressService;
import com.electronics.sa.util.ResponseEntityProxy;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService{

	private AddressRepository addressRepository;

	private StoreRepository storeRepository;

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddress(AddressRequest addressRequest, int storeId) 
	{
		Address address = addressRepository.save(mapToAddress(addressRequest));
		if(storeId !=0)
		{
			storeRepository.findById(storeId).map(store -> {
				store.setAddress(address);
				return storeRepository.save(store);
			});
		}

		return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "Address details added successfully", mapToAddressResponse(address));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(AddressRequest addressRequest, int addressId) 
	{
		return addressRepository.findById(addressId).map(address -> {

			Address updatedAddress = mapToAddress(addressRequest);
			updatedAddress.setAddressId(address.getAddressId());
			updatedAddress = addressRepository.save(updatedAddress);
			return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "Address details updated successfully", mapToAddressResponse(updatedAddress));

		}).orElseThrow(() -> new AddressNotFoundException("Address with the given Id not found, please provide a valid address Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(int addressId)
	{
		return addressRepository.findById(addressId).map(address -> {

			return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Address details found successfully", mapToAddressResponse(address));

		}).orElseThrow(() -> new AddressNotFoundException("Address with the given Id not found, please provide a valid address Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(int storeId) 
	{
		return storeRepository.findById(storeId).map(store -> {

			Address address = store.getAddress();
			if(address != null)
				return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Address details found successfully", mapToAddressResponse(address));
			else
				throw new AddressNotFoundException("Address not found for the given store Id");

		}).orElseThrow(() -> new StoreNotFoundException("Store with the given Id not found, please provide a valid store Id"));
	}

	//Mapper Methods
	private AddressResponse mapToAddressResponse(Address address)
	{
		return AddressResponse.builder()
				.addressId(address.getAddressId())
				.streetAddress(address.getStreetAddress())
				.streetAddressAdditional(address.getStreetAddressAdditional())
				.city(address.getCity())
				.state(address.getState())
				.country(address.getCountry())
				.pincode(address.getPincode())
				.addressType(address.getAddressType().toString())
				.build();
	}

	private Address mapToAddress(AddressRequest addressRequest) 
	{
		try {
			AddressType addressType = AddressType.valueOf(addressRequest.getAddressType().toUpperCase());

			return Address.builder()
					.streetAddress(addressRequest.getStreetAddress())
					.streetAddressAdditional(addressRequest.getStreetAddressAdditional())
					.city(addressRequest.getCity())
					.state(addressRequest.getState())
					.Country(addressRequest.getCountry())
					.pincode(addressRequest.getPincode())
					.addressType(addressType)
					.build();
		}
		catch(IllegalArgumentException | NullPointerException ex)
		{
			throw new InvalidAddressTypeException("The Address Type can be Residential or Official only");
		}
	}

}
