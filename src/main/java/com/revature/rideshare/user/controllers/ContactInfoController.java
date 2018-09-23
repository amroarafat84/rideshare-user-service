package com.revature.rideshare.user.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.user.beans.ContactInfo;
import com.revature.rideshare.user.beans.ResponseError;
import com.revature.rideshare.user.services.ContactInfoService;

@RestController
public class ContactInfoController {
	@Autowired
	ContactInfoService contactInfoService;

	@RequestMapping(value="/contact-info", method = RequestMethod.GET)
	public ResponseEntity<List<ContactInfo>> findAll() {
		List<ContactInfo> users = contactInfoService.findAll();
		return ResponseEntity.ok(users);
	}

	@RequestMapping(value = "/contact-info/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable("id") int id) {
		ContactInfo contactInfo = contactInfoService.findById(id);
		return contactInfo == null ? new ResponseError("Contact info with ID " + id + " does not exist.")
				.toResponseEntity(HttpStatus.NOT_FOUND) : ResponseEntity.ok(contactInfo);
	}

	@RequestMapping(value = "/contact-info", method = RequestMethod.POST)
	public ResponseEntity<ContactInfo> add(@RequestBody @Valid ContactInfo info) {
		info.setId(0);
		return ResponseEntity.ok(contactInfoService.save(info));
	}

	@RequestMapping(value = "/contact-info/{id}", method = RequestMethod.PUT)
	public ResponseEntity<ContactInfo> update(@PathVariable int id, @RequestBody @Valid ContactInfo info) {
		info.setId(id);
		return ResponseEntity.ok(contactInfoService.save(info));
	}
}
