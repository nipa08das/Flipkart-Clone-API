package com.electronics.sa.request_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

	private String streetAddress;
	private String streetAddressAdditional;
	private String city;
	private String state;
	private String Country;
	private int pincode;
	private String addressType;
}
