package com.electronics.sa.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.electronics.sa.entity.User;

@Configuration
public class CacheConfigBean {

	@Bean
	CacheStrore<String> otpCacheStrore(){
		return new CacheStrore<>(Duration.ofMinutes(5));
	}
	
	@Bean
	CacheStrore<User> userCacheStrore(){
		return new CacheStrore<>(Duration.ofMinutes(5));
	}
	
}
