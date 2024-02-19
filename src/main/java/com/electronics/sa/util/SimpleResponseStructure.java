package com.electronics.sa.util;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class SimpleResponseStructure {

	private int status;
	private String message;
}
