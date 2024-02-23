package com.electronics.sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
