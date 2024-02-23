package com.electronics.sa.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.electronics.sa.request_dto.ContactRequest;
import com.electronics.sa.response_dto.ContactResponse;
import com.electronics.sa.util.ResponseStructure;

public interface ContactService {

	ResponseEntity<ResponseStructure<ContactResponse>> addContact(ContactRequest contactRequest);

	ResponseEntity<ResponseStructure<ContactResponse>> updateContact(ContactRequest contactRequest, int contactId);

	ResponseEntity<ResponseStructure<ContactResponse>> findContactById(int contactId);

	ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactByAddress(int addressId);

}
