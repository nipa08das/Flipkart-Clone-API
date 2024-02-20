package com.electronics.sa.response_dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreResponse {

	private int storeId;
	private String storeName;
	private String logoLink;
	private String about;
}
