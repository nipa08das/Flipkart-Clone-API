package com.electronics.sa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.RefreshToken;
import com.electronics.sa.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

	Optional<RefreshToken> findByToken(String refreshToken);

	List<RefreshToken> findAllByUserAndIsBlocked(User user, boolean b);

	List<RefreshToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	boolean existsByTokenAndIsBlockedAndExpirationAfter(String refreshToken, boolean b, LocalDateTime now);

}
