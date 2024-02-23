package com.electronics.sa.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.electronics.sa.entity.Contact;
import com.electronics.sa.enums.Priority;
import com.electronics.sa.exception.AddressNotFoundException;
import com.electronics.sa.exception.ContactNotFoundException;
import com.electronics.sa.exception.InvalidPriorityException;
import com.electronics.sa.repository.AddressRepository;
import com.electronics.sa.repository.ContactRepository;
import com.electronics.sa.request_dto.ContactRequest;
import com.electronics.sa.response_dto.ContactResponse;
import com.electronics.sa.service.ContactService;
import com.electronics.sa.util.ResponseEntityProxy;
import com.electronics.sa.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService{

	private ContactRepository contactRepository;
	
	private AddressRepository addressRepository;

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> addContact(ContactRequest contactRequest) 
	{		
		Contact contact = contactRepository.save(mapToContact(contactRequest));

		return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "Contact details added successfully", mapToContactReponse(contact));
	}

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(ContactRequest contactRequest, int contactId) 
	{
		return contactRepository.findById(contactId).map(contact -> {
			Contact updatedContact = mapToContact(contactRequest);
			updatedContact.setContactId(contact.getContactId());
			updatedContact = contactRepository.save(updatedContact);
			return ResponseEntityProxy.getResponseEntity(HttpStatus.OK, "Contact details updated successfully", mapToContactReponse(updatedContact));
			
		}).orElseThrow(() -> new ContactNotFoundException("Contact with the given Id not found, please provide a valid contact Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> findContactById(int contactId) 
	{
		return contactRepository.findById(contactId).map(contact -> {
			
			return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Contact details found successfully", mapToContactReponse(contact));
		
		}).orElseThrow(() -> new ContactNotFoundException("Contact with the given Id not found, please provide a valid contact Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactByAddress(int addressId) 
	{
		return addressRepository.findById(addressId).map(address -> {
			List<Contact> contacts = contactRepository.findByAddress(address);
			if(contacts.isEmpty())
				throw new ContactNotFoundException("Contacts not found for the given address Id");
			else
			{
				List<ContactResponse> contactResponses = contacts.stream().map(this::mapToContactReponse).collect(Collectors.toList());
				return ResponseEntityProxy.getResponseEntity(HttpStatus.FOUND, "Contact details found successfully", contactResponses);
			}
		}).orElseThrow(() -> new AddressNotFoundException("Address with the given Id not found, please provide a valid address Id"));
	
	}

	//Mapper Methods
	private ContactResponse mapToContactReponse(Contact contact)
	{
		return ContactResponse.builder()
				.contactId(contact.getContactId())
				.contactName(contact.getContactName())
				.contactNumber(contact.getContactNumber())
				.priority(contact.getPriority().toString())
				.build();
	}

	private Contact mapToContact(ContactRequest contactRequest)
	{
		try {
			Priority priority = Priority.valueOf(contactRequest.getPriority().toUpperCase());

			return Contact.builder()
					.contactName(contactRequest.getContactName())
					.contactNumber(contactRequest.getContactNumber())
					.priority(priority)
					.build();
		}
		catch(IllegalArgumentException | NullPointerException ex)
		{
			throw new InvalidPriorityException("The priority can be Primary or Secondary only");
		}
	}

}
