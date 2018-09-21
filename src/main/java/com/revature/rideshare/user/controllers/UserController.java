package com.revature.rideshare.user.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.user.beans.ResponseError;
import com.revature.rideshare.user.beans.User;
import com.revature.rideshare.user.beans.UserRegistrationInfo;
import com.revature.rideshare.user.jsonbeans.JsonUser;
import com.revature.rideshare.user.jsonbeans.JsonUserRegistrationInfo;
import com.revature.rideshare.user.jsonbeans.UserConverter;
import com.revature.rideshare.user.services.UserService;

@RestController
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	UserConverter userConverter;
	
	@RequestMapping(value="/users", method = RequestMethod.GET)
	public ResponseEntity<List<JsonUser>> findAll() {
		List<User> users = userService.findAll();
		return ResponseEntity.ok(users.stream().map(userConverter::toJson).collect(Collectors.toList()));
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable("id") int id) {
		User user = userService.findById(id);
		return user == null
				? new ResponseError("User with ID " + id + " does not exist.").toResponseEntity(HttpStatus.NOT_FOUND)
				: ResponseEntity.ok(userConverter.toJson(user));
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET, params = "email")
	public ResponseEntity<?> findByEmail(@RequestParam("email") @NotEmpty String email) {
		User user = userService.findByEmail(email);
		return user == null ? new ResponseError("User with email " + email + " does not exist.")
				.toResponseEntity(HttpStatus.NOT_FOUND) : ResponseEntity.ok(userConverter.toJson(user));
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ResponseEntity<JsonUser> add(@RequestBody @Valid JsonUserRegistrationInfo registration) {
		registration.getUser().setId(0);
		User result = userService.register(
				new UserRegistrationInfo(userConverter.fromJson(registration.getUser()), registration.getPassword()));
		if (result != null) {
			return new ResponseEntity<JsonUser>(userConverter.toJson(result), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<JsonUser>(userConverter.toJson(result), HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	public ResponseEntity<User> update(@PathVariable("id") int id, @RequestBody @Valid JsonUser user) {
		user.setId(id);
		User result = userService.save(userConverter.fromJson(user));
		if (result != null) {
			return new ResponseEntity<User>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(result, HttpStatus.CONFLICT);
		}
	}
}
