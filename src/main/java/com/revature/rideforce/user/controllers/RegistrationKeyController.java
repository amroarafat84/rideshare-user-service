package com.revature.rideforce.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideforce.user.beans.ResponseError;
import com.revature.rideforce.user.beans.User;
import com.revature.rideforce.user.security.RegistrationTokenProvider;
import com.revature.rideforce.user.services.AuthenticationService;

@RestController
@RequestMapping("/registration-key")
public class RegistrationKeyController {
	@Autowired
	private RegistrationTokenProvider tokenProvider;

	@Autowired
	private AuthenticationService authenticationService;

	/**
	 * the link where you can get a registration token. Only admins and trainers should be able to generate the token for 
	 * someone to register
	 * @return <code>String</code> from {@linkplain RegistrationTokenProvider#generateToken()}
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> get() {
		// Only trainers and admins can get registration tokens.
		User loggedIn = authenticationService.getCurrentUser();
		if (!loggedIn.isTrainer() && !loggedIn.isAdmin()) {
			return new ResponseError("Only trainers and admins can generate registration tokens.")
					.toResponseEntity(HttpStatus.FORBIDDEN);
		}
		return ResponseEntity.ok('"' + tokenProvider.generateToken() + '"');
	}
}
