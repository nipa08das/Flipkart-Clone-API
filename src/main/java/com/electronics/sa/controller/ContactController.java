package com.electronics.sa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electronics.sa.request_dto.ContactRequest;
import com.electronics.sa.response_dto.ContactResponse;
import com.electronics.sa.service.ContactService;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173")
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ContactController {

	private ContactService contactService;
	
	@PostMapping("/contacts")
	public ResponseEntity<ResponseStructure<ContactResponse>> addContact(@RequestBody ContactRequest contactRequest)
	{
		return contactService.addContact(contactRequest);
	}
	
	@PutMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(@RequestBody ContactRequest contactRequest, @PathVariable int contactId)
	{
		return contactService.updateContact(contactRequest, contactId);
	}
	
	@GetMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> findContactById(@PathVariable int contactId)
	{
		return contactService.findContactById(contactId);
	}
	
	@GetMapping("/addresses/{addressId}/contacts")
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactByAddress(@PathVariable int addressId)
	{
		return contactService.findContactByAddress(addressId);
	}
}
