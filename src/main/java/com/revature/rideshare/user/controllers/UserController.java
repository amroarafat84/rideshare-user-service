package com.revature.rideshare.user.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.user.beans.Office;
import com.revature.rideshare.user.beans.ResponseError;
import com.revature.rideshare.user.beans.User;
import com.revature.rideshare.user.beans.UserRegistrationInfo;
import com.revature.rideshare.user.beans.UserRole;
import com.revature.rideshare.user.exceptions.EmailAlreadyUsedException;
import com.revature.rideshare.user.exceptions.InvalidRegistrationKeyException;
import com.revature.rideshare.user.services.OfficeService;
import com.revature.rideshare.user.services.UserRoleService;
import com.revature.rideshare.user.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	OfficeService officeService;

	@Autowired
	UserRoleService userRoleService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<User>> findAll() {
		return ResponseEntity.ok(userService.findAll());
	}

	@GetMapping(params = "email", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> findByEmail(@RequestParam("email") @NotEmpty String email) {
		User user = userService.findByEmail(email);
		return user == null ? new ResponseError("User with email " + email + " does not exist.")
				.toResponseEntity(HttpStatus.NOT_FOUND) : ResponseEntity.ok(user);
	}

	@GetMapping(params = { "office", "role" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> findByOfficeAndRole(@RequestParam("office") int officeId,
			@RequestParam("role") String roleString) {
		Office office = officeService.findById(officeId);
		if (office == null) {
			return new ResponseError("Office with ID " + officeId + " does not exist.")
					.toResponseEntity(HttpStatus.BAD_REQUEST);
		}
		UserRole role = userRoleService.findByType(roleString);
		if (role == null) {
			return new ResponseError(roleString + " is not a valid user role.")
					.toResponseEntity(HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(userService.findByOfficeAndRole(office, role));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> findById(@PathVariable("id") int id) {
		User user = userService.findById(id);
		return user == null
				? new ResponseError("User with ID " + id + " does not exist.").toResponseEntity(HttpStatus.NOT_FOUND)
				: ResponseEntity.ok(user);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> add(@RequestBody @Valid UserRegistrationInfo registration) {
		try {
			User created = userService.register(registration);
			return ResponseEntity.created(created.toUri()).body(created);
		} catch (InvalidRegistrationKeyException e) {
			return new ResponseError(e).toResponseEntity(HttpStatus.FORBIDDEN);
		} catch (EmailAlreadyUsedException e) {
			return new ResponseError(e).toResponseEntity(HttpStatus.CONFLICT);
		}
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> save(@PathVariable("id") int id, @RequestBody @Valid User user) {
		user.setId(id);
		try {
			return ResponseEntity.ok(userService.save(user));
		} catch (EmailAlreadyUsedException e) {
			return new ResponseError(e).toResponseEntity(HttpStatus.CONFLICT);
		}
	}
}
