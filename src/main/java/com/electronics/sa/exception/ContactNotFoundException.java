package com.electronics.sa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactNotFoundException extends RuntimeException{

	private String message;
}
