package com.electronics.sa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
