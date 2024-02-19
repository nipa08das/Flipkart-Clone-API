package com.electronics.sa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.AccessToken;
import com.electronics.sa.entity.User;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer>{

	Optional<AccessToken> findByToken(String accessToken);

	List<AccessToken> findAllByUserAndIsBlocked(User user, boolean b);

	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);

}
