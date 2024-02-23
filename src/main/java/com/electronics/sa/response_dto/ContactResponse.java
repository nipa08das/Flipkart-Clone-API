package com.electronics.sa.response_dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContactResponse {

	private int contactId;
	private String contactName;
	private long contactNumber;
	private String priority;
}
