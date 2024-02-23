package com.electronics.sa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electronics.sa.entity.Address;
import com.electronics.sa.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	List<Contact> findByAddress(Address address);

}
