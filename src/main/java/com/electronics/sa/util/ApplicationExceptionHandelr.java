package com.electronics.sa.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.electronics.sa.exception.InvalidOtpException;
import com.electronics.sa.exception.InvalidUserRoleException;
import com.electronics.sa.exception.OtpExpiredException;
import com.electronics.sa.exception.RefreshTokenExpiredException;
import com.electronics.sa.exception.SessionExpiredException;
import com.electronics.sa.exception.UniqueConstraintViolationException;
import com.electronics.sa.exception.UserAlreadyLoggedInException;
import com.electronics.sa.exception.UserNotFoundException;

@RestControllerAdvice
public class ApplicationExceptionHandelr extends ResponseEntityExceptionHandler{

	public ResponseEntity<Object> exceptionStructure(HttpStatus status, String message, Object rootcause)
	{
		return new ResponseEntity<Object>(
				Map.of("status", status.value(),
						"message", message,
						"rootcause", rootcause), status);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) 
	{
		List<ObjectError> allErrors = ex.getAllErrors();
		Map<String, String> errors = new HashMap<String, String>(); 
		
		allErrors.forEach(error -> {
			FieldError fieldError = (FieldError) error;
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		});
		
		return exceptionStructure(HttpStatus.BAD_REQUEST, "Validations failed for some inputs, please check your fields properly", errors);
	}
	
	@ExceptionHandler(UniqueConstraintViolationException.class)
	public ResponseEntity<Object> handleUniqueConstraintViolation(UniqueConstraintViolationException ex)
	{
		return exceptionStructure(HttpStatus.IM_USED, ex.getMessage(), "The fields should be unique");
	}
	
	@ExceptionHandler(InvalidUserRoleException.class)
	public ResponseEntity<Object> handleInvalidUserRole(InvalidUserRoleException ex)
	{
		return exceptionStructure(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid User Role");
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex)
	{
		return exceptionStructure(HttpStatus.NOT_FOUND, ex.getMessage(), "Username not found");
	}
	
	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<Object> handleOtpExpired(OtpExpiredException ex)
	{
		return exceptionStructure(HttpStatus.FORBIDDEN, ex.getMessage(), "Otp has expired");
	}
	
	@ExceptionHandler(SessionExpiredException.class)
	public ResponseEntity<Object> handleSessionExpired(SessionExpiredException ex)
	{
		return exceptionStructure(HttpStatus.FORBIDDEN, ex.getMessage(), "User session has expired");
	}
	
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<Object> handleInvalidOtp(InvalidOtpException ex)
	{
		return exceptionStructure(HttpStatus.BAD_REQUEST, ex.getMessage(), "The Otp is Invalid");
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex)
	{
		return exceptionStructure(HttpStatus.NON_AUTHORITATIVE_INFORMATION, ex.getMessage(), "User not found");
	}
	
	@ExceptionHandler(UserAlreadyLoggedInException.class)
	public ResponseEntity<Object> handleUserAlreadyLoggedIn(UserAlreadyLoggedInException ex)
	{
		return exceptionStructure(HttpStatus.IM_USED, ex.getMessage(), "User already login");
	}
	
	@ExceptionHandler(RefreshTokenExpiredException.class)
	public ResponseEntity<Object> handleRefreshTokenExpired(RefreshTokenExpiredException ex)
	{
		return exceptionStructure(HttpStatus.FORBIDDEN, ex.getMessage(), "Refresh Token Expired");
	}
}
