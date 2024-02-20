package com.electronics.sa.serviceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.electronics.sa.cache.CacheStrore;
import com.electronics.sa.entity.AccessToken;
import com.electronics.sa.entity.Customer;
import com.electronics.sa.entity.RefreshToken;
import com.electronics.sa.entity.Seller;
import com.electronics.sa.entity.User;
import com.electronics.sa.enums.UserRole;
import com.electronics.sa.exception.InvalidOtpException;
import com.electronics.sa.exception.InvalidUserRoleException;
import com.electronics.sa.exception.OtpExpiredException;
import com.electronics.sa.exception.RefreshTokenExpiredException;
import com.electronics.sa.exception.SessionExpiredException;
import com.electronics.sa.exception.UniqueConstraintViolationException;
import com.electronics.sa.exception.UserAlreadyLoggedInException;
import com.electronics.sa.exception.UserNotFoundException;
import com.electronics.sa.repository.AccessTokenRepository;
import com.electronics.sa.repository.CustomerRepository;
import com.electronics.sa.repository.RefreshTokenRepository;
import com.electronics.sa.repository.SellerRepository;
import com.electronics.sa.repository.UserRepository;
import com.electronics.sa.request_dto.AuthRequest;
import com.electronics.sa.request_dto.OtpModel;
import com.electronics.sa.request_dto.UserRequest;
import com.electronics.sa.response_dto.AuthResponse;
import com.electronics.sa.response_dto.UserResponse;
import com.electronics.sa.security.JWTService;
import com.electronics.sa.service.AuthService;
import com.electronics.sa.util.CookieManager;
import com.electronics.sa.util.MessageStructure;
import com.electronics.sa.util.ResponseEntityProxy;
import com.electronics.sa.util.ResponseStructure;
import com.electronics.sa.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private SellerRepository sellerRepository;

	private CustomerRepository customerRepository;

	private UserRepository userRepository;

	private PasswordEncoder encoder;

	private CacheStrore<User> userCacheStore;

	private CacheStrore<String> otpCacheStore;

	private JavaMailSender javaMailSender;

	private AuthenticationManager authenticationManager;

	private JWTService jwtService;

	private CookieManager cookieManager;

	private AccessTokenRepository accessTokenRepository;

	private RefreshTokenRepository refreshTokenRepository;

	private SimpleResponseStructure simpleResponseStructure;

	public AuthServiceImpl(SellerRepository sellerRepository, CustomerRepository customerRepository,
			UserRepository userRepository, PasswordEncoder encoder, CacheStrore<User> userCacheStore,
			CacheStrore<String> otpCacheStore, JavaMailSender javaMailSender,
			AuthenticationManager authenticationManager, JWTService jwtService, CookieManager cookieManager,
			AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository,
			SimpleResponseStructure simpleResponseStructure) {
		super();
		this.sellerRepository = sellerRepository;
		this.customerRepository = customerRepository;
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.userCacheStore = userCacheStore;
		this.otpCacheStore = otpCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.cookieManager = cookieManager;
		this.accessTokenRepository = accessTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.simpleResponseStructure = simpleResponseStructure;
	}

	@Value("${myapp.access.expiry}")
	private int accessExpirationInSeconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpirationInSeconds;

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) 
	{
		if(userRepository.existsByEmail(userRequest.getEmail()))
			throw new UniqueConstraintViolationException("The user is already registered, please sign In");
		String OTP = generateOTP();
		User user = mapToUser(userRequest);
		userCacheStore.add(userRequest.getEmail(), user);
		otpCacheStore.add(userRequest.getEmail(),OTP);
		try {
			sendOtpToMail(user, OTP);
		} catch (MessagingException e) {
			log.error("The Email Address Doesn't Exist");
		}

		return ResponseEntityProxy.getResponseEntity(HttpStatus.ACCEPTED, "User created successfully, verify your email Id", mapToUserResponse(user));
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel) 
	{
		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());

		if(otp == null) 
			throw new OtpExpiredException("The OTP has Expired, try Once Again");
		if(user == null) 
			throw new SessionExpiredException("The Registration Session is Expired");
		if(!otp.equals(otpModel.getOtp()))
			throw new InvalidOtpException("The Given OTP is Invalid");

		user.setEmailVerified(true);
		user = saveUser(user);
		try {
			sendResponseToMail(user);
		} catch (MessagingException e) {
			log.error("Process Incomplete due To Miss Consumption");
		}

		return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "User Registration Successfully Completed!!", mapToUserResponse(user));
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,
			HttpServletResponse httpServletResponse,String accessToken, String refreshToken) 
	{
		if(accessToken != null || refreshToken != null)
			throw new UserAlreadyLoggedInException("You are already loged in to the account");

		String username = authRequest.getEmail().split("@")[0];

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
				authRequest.getPassword());

		Authentication authenticate = authenticationManager.authenticate(token);

		if (!authenticate.isAuthenticated())
			throw new UserNotFoundException("Failed to authenticate the user, please give a valid Email and Password");
		else {
			// generating the cookies and authResponse and returning to the client

			return userRepository.findByUsername(username).map(user -> {
				grantAccess(httpServletResponse, user);

				return ResponseEntityProxy.getResponseEntity(HttpStatus.OK, "Log In Successfull", 
						AuthResponse.builder()
						.userId(user.getUserId())
						.username(user.getUsername())
						.userRole(user.getUserRole().toString())
						.isAuthenticated(authenticate.isAuthenticated())
						.accessExpiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
						.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
						.build());
			}).get();
		}
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> logoutTraditional(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) 
	{
		String accessToken = "";
		String refreshToken = "";

		Cookie[] cookies = httpServletRequest.getCookies();
		for(Cookie cookie : cookies)
		{
			if(cookie.getName().equals("accessToken"))
				accessToken = cookie.getValue();
			else if(cookie.getName().equals("refreshToken"))
				refreshToken = cookie.getValue();

			accessTokenRepository.findByToken(accessToken).ifPresent(at -> {
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});

			refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});

			httpServletResponse.addCookie(cookieManager.invalidate(new Cookie("accessToken", "")));
			httpServletResponse.addCookie(cookieManager.invalidate(new Cookie("refreshToken", "")));
		}

		simpleResponseStructure.setStatus(HttpStatus.OK.value());
		simpleResponseStructure.setMessage("Logout Successfull");
		return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse) 
	{
		if(accessToken != null && refreshToken != null)
		{
			accessTokenRepository.findByToken(accessToken).ifPresent(at -> {
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});

			refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});

			httpServletResponse.addCookie(cookieManager.invalidate(new Cookie("accessToken", "")));
			httpServletResponse.addCookie(cookieManager.invalidate(new Cookie("refreshToken", "")));

			simpleResponseStructure.setStatus(HttpStatus.OK.value());
			simpleResponseStructure.setMessage("Logout successfull");
			return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
		}
		else {
			throw new UserNotFoundException("User not logged In, please log In first to Logout");
		}
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAllDevice() 
	{
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		userRepository.findByUsername(userName).ifPresent(user -> {

			blockAccessToken(accessTokenRepository.findAllByUserAndIsBlocked(user, false));
			blockRefreshToken(refreshTokenRepository.findAllByUserAndIsBlocked(user, false));

			simpleResponseStructure.setMessage(" Revoked all current users ");
			simpleResponseStructure.setStatus(HttpStatus.OK.value());

		});

		return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeOtherDevice(HttpServletResponse httpServletResponse,
			String accessToken, String refreshToken) 
	{
		userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(user -> {
			blockAccessToken(accessTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));

			blockRefreshToken(refreshTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user, false, refreshToken));

			simpleResponseStructure.setMessage("successfully revoked other devices ");
			simpleResponseStructure.setStatus(HttpStatus.OK.value());
		});

		return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshToken(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse) 
	{
		String username = null;
		if (refreshToken == null)
			throw new RefreshTokenExpiredException("refresh token has expired ");		

		boolean existsRefreshToken = refreshTokenRepository.existsByTokenAndIsBlockedAndExpirationAfter(refreshToken, false, LocalDateTime.now());
		if(existsRefreshToken)
			username = jwtService.extractUsername(refreshToken);

		refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
			rt.setBlocked(true);
			refreshTokenRepository.save(rt);
		});

		if (accessToken != null) {
			accessTokenRepository.findByToken(accessToken).ifPresent(token -> {
				token.setBlocked(true);
				accessTokenRepository.save(token);
			});
		}

		return userRepository.findByUsername(username).map(user -> {
			grantAccess(httpServletResponse, user);

			return ResponseEntityProxy.getResponseEntity(HttpStatus.OK, "Refreshed the token cycle	", 
					AuthResponse.builder()
					.userId(user.getUserId())
					.username(user.getUsername())
					.userRole(user.getUserRole().toString())
					.isAuthenticated(true)
					.accessExpiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
					.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
					.build());
		}).orElseThrow(() -> new UserNotFoundException("  user not  logged In "));
	}	


	private void blockAccessToken(List<AccessToken> accessTokens) {
		accessTokens.forEach(accessToken -> {
			accessToken.setBlocked(true);
			accessTokenRepository.save(accessToken);
		});
	}

	private void blockRefreshToken(List<RefreshToken> refreshTokens) {
		refreshTokens.forEach(refreshToken -> {
			refreshToken.setBlocked(true);
			refreshTokenRepository.save(refreshToken);
		});
	}

	private void grantAccess(HttpServletResponse httpServletResponse, User user)
	{
		// generating access and refresh tokens

		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());

		//adding access and refresh tokens cookie to the response 

		httpServletResponse.addCookie(cookieManager.configure(new Cookie("accessToken", accessToken), accessExpirationInSeconds));
		httpServletResponse.addCookie(cookieManager.configure(new Cookie("refreshToken", refreshToken), refreshExpirationInSeconds));

		// saving the access and refresh cookie in to the database

		accessTokenRepository.save(AccessToken.builder()
				.token(accessToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
				.user(user)
				.build());

		refreshTokenRepository.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
				.user(user)
				.build());
	}

	private void sendOtpToMail(User user ,String otp) throws MessagingException
	{
		sendMail( MessageStructure.builder()
				.to(user.getEmail())
				.subject("Complete Your Registration to E-Commerce Application")
				.sentDate(new Date())
				.text(
						"hey, "+user.getUsername()
						+"<h3>Good To See that you are Intrested in Our E-Commerce Application,<h3>"
						+"<h3>Complete Your Registration Using the OTP<h3> <br>"
						+"<h1>"+otp+"<h1><br>"
						+"<h3>Note: The OTP will Expired In 5 Minutes<h3>"
						+"<br><br>"
						+"<h3>With Best Regards<h3><br>"
						+"<h1>E-Commerce Application<h1>"
						).build());
	}	
	private void sendResponseToMail(User user) throws MessagingException {

		sendMail( MessageStructure.builder()
				.to(user.getEmail())
				.subject("Welcome to E-Commerce Api!")
				.sentDate(new Date())
				.text(
						"Dear, "+"<h2>"+user.getUsername()+"<h2>"
								+"<h3>Congratulations! 🎉..,Good To See You in our application,<h3>"
								+"<h3>You have sucessfully Completed Your Registration to E-Commerce Application<h3> <br>"
								+"<h3>Your email has been successfully verified, and you're now officially registered to our Application.<h3>"
								+"<br>"
								+"<h3>Let's get started on your journey! 🚀<h3>"
								+"<br>"
								+"<h3>With Best Regards<h3><br>"
								+"<h2>Team Flipkart<h2>"
						).build());

	}

	@Async
	private void sendMail(MessageStructure message) throws MessagingException{
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
		messageHelper.setTo(message.getTo());
		messageHelper.setSubject(message.getSubject());
		messageHelper.setSentDate(message.getSentDate());
		messageHelper.setText(message.getText(),true); 
		javaMailSender.send(mimeMessage);
	}

	private String generateOTP() 
	{
		return String.valueOf(new Random().nextInt(100000, 999999));
	}


	private UserResponse mapToUserResponse(User user) 
	{
		return UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}

	@SuppressWarnings("unchecked")
	private <T extends User> T saveUser(User user) 
	{
		User user1 = null;
		if(user instanceof Seller)
			user1 = sellerRepository.save((Seller)user);
		else
			user1 = customerRepository.save((Customer) user);
		return (T) user1;
	}

	@SuppressWarnings("unchecked")
	private <T extends User> T mapToUser(UserRequest userRequest)
	{
		User user = null;
		try {
			UserRole userRole = UserRole.valueOf(userRequest.getUserRole().toUpperCase());

			switch(userRole)
			{
			case UserRole.SELLER : user = new Seller();
			break;
			case UserRole.CUSTOMER : user = new Customer();
			break;
			default : throw new InvalidUserRoleException("The user role can be Seller and Customer only"); 
			}
			user.setUsername(userRequest.getEmail().split("@")[0]);
			user.setEmail(userRequest.getEmail());
			user.setPassword(encoder.encode(userRequest.getPassword()));
			user.setUserRole(userRole);
		}
		catch(IllegalArgumentException | NullPointerException ex)
		{
			throw new InvalidUserRoleException("The user role can be Seller and Customer only");
		}
		return (T) user;
	}

}
