package com.electronics.sa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electronics.sa.request_dto.AuthRequest;
import com.electronics.sa.request_dto.OtpModel;
import com.electronics.sa.request_dto.UserRequest;
import com.electronics.sa.response_dto.AuthResponse;
import com.electronics.sa.response_dto.UserResponse;
import com.electronics.sa.service.AuthService;
import com.electronics.sa.util.ResponseStructure;
import com.electronics.sa.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
@RequestMapping("/api/v1")
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest)
	{
		return authService.registerUser(userRequest);
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpModel otpModel)
	{
		return authService.verifyOTP(otpModel);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,
			HttpServletResponse httpServletResponse,
			@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken)
	{
		return authService.login(authRequest, httpServletResponse, accessToken, refreshToken);
	}
	
	@PostMapping("/logout-traditional")
	public ResponseEntity<SimpleResponseStructure> logoutTraditional(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
	{
		return authService.logoutTraditional(httpServletRequest, httpServletResponse);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<SimpleResponseStructure> logout(
			@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken,
			HttpServletResponse httpServletResponse)
	{
		return authService.logout(accessToken, refreshToken, httpServletResponse);
	}
	
	@PostMapping("/revoke-all")
	public ResponseEntity<SimpleResponseStructure> revokeAllDevice() 
	{
		return authService.revokeAllDevice();
	}
	
	@PostMapping("/revoke-other")
	public ResponseEntity<SimpleResponseStructure> revokeOtherDevice(HttpServletResponse httpServletResponse,
			@CookieValue(name = "accessToken", required = true) String accessToken,
			@CookieValue(name = "refreshToken", required = true) String refreshToken) 
	{
		return authService.revokeOtherDevice(httpServletResponse, accessToken, refreshToken);
	}
	
	@PostMapping("refresh-token")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshToken(HttpServletResponse response,
			@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken)
	{
		return authService.refreshToken(accessToken, refreshToken, response);
	}
}
