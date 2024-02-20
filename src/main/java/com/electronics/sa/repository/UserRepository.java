package com.electronics.sa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.User;
import com.electronics.sa.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String string);

	boolean existsByEmail(String email);

	List<User> findByIsEmailVerified(boolean b);

	boolean existsByEmailAndUserRole(String email, UserRole userRole);

}
