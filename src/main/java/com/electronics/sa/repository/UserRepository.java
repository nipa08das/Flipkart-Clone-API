package com.electronics.sa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String string);

	boolean existsByEmail(String email);

	List<User> findByIsEmailVerified(boolean b);

}
