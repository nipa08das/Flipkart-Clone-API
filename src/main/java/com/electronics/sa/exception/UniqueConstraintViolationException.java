package com.electronics.sa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UniqueConstraintViolationException extends RuntimeException {

	private String message;
}
