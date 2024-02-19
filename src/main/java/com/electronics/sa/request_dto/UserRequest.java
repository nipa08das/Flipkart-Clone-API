package com.electronics.sa.request_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

	private String email;
	private String password;
	private String userRole;
}
