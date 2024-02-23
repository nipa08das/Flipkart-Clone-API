package com.electronics.sa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidAddressTypeException extends RuntimeException{

	private String message;
}
