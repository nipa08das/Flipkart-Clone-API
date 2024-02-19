package com.electronics.sa.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.electronics.sa.entity.AccessToken;
import com.electronics.sa.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private AccessTokenRepository accessTokenRepository;

	private JWTService jwtService;

	private CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("");
		Cookie[] cookies = request.getCookies();

		String at = null, rt = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("accessToken"))
					at = cookie.getValue();
				if (cookie.getName().equals("refreshToken"))
					rt = cookie.getValue();
			}

			if (at != null && rt != null) {
				Optional<AccessToken> accessToken = accessTokenRepository.findByTokenAndIsBlocked(at, false);

				if (accessToken.isPresent()) {

					String username = jwtService.extractUsername(at);

					if (username != null) {
						UserDetails user = customUserDetailsService.loadUserByUsername(username);

						UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
								null, user.getAuthorities());
						token.setDetails(new WebAuthenticationDetails(request));
						SecurityContextHolder.getContext().setAuthentication(token);
					}

				}
			}

		}
		filterChain.doFilter(request, response);// it will delegates the current filter to the next filter
	}
}
