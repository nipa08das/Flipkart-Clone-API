package com.electronics.sa.request_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

	private String email;
	private String password;
}
