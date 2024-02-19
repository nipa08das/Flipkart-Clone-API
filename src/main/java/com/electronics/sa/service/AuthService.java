package com.electronics.sa.service;

import org.springframework.http.ResponseEntity;

import com.electronics.sa.request_dto.AuthRequest;
import com.electronics.sa.request_dto.OtpModel;
import com.electronics.sa.request_dto.UserRequest;
import com.electronics.sa.response_dto.AuthResponse;
import com.electronics.sa.response_dto.UserResponse;
import com.electronics.sa.util.ResponseStructure;
import com.electronics.sa.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,
			HttpServletResponse httpServletResponse, String accessToken, String refreshToken);

	ResponseEntity<SimpleResponseStructure> logoutTraditional(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse);

	ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse);

	ResponseEntity<SimpleResponseStructure> revokeAllDevice();

	ResponseEntity<SimpleResponseStructure> revokeOtherDevice(HttpServletResponse httpServletResponse,
			String accessToken, String refreshToken);

	ResponseEntity<ResponseStructure<AuthResponse>> refreshToken(String accessToken, String refreshToken,
			HttpServletResponse response);

}
