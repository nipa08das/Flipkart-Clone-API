package com.electronics.sa.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieManager {

	@Value("${myapp.domain}")
	private String domain;
	
	public Cookie configure(Cookie cookie, int expirationInSeconds) {
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expirationInSeconds);
		cookie.setPath("/");
		return cookie;
	}

	// to remove the cookie from the browser
	
	public Cookie invalidate(Cookie cookie) {
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}
}
