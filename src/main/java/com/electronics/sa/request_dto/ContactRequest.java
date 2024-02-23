package com.electronics.sa.request_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {

	private String contactName;
	private long contactNumber;
	private String priority;
}
