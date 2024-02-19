package com.electronics.sa.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	@Value("${myapp.secret}")
	private String secret;
	
	@Value("${myapp.access.expiry}")
	private long accessExpirationInSeconds;

	@Value("${myapp.refresh.expiry}")
	private long refreshExpirationInSeconds;
	
	public String generateAccessToken(String userName) {
		return generateJWT(new HashMap<String, Object>(), userName, accessExpirationInSeconds * 1000L);
	}

	public String generateRefreshToken(String userName) {
		return generateJWT(new HashMap<String, Object>(), userName, refreshExpirationInSeconds * 1000L);
	}
	
	private String generateJWT(Map<String, Object> claims, String userName, Long expiry) {
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiry))
				.signWith(getSignature(), SignatureAlgorithm.HS512).compact();
	}

	private Key getSignature() 
	{
		byte[] secreteBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(secreteBytes);
	}

	private Claims jwtParser(String token) {
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSignature()).build();
		return jwtParser.parseClaimsJws(token).getBody(); // it will returns the claims which is consist in the body
	}
	
	public String extractUsername(String token) 
	{
		return jwtParser(token).getSubject(); // it will returns the user name
	}

}
