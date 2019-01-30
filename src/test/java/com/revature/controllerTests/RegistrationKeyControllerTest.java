package com.revature.controllerTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.revature.rideforce.user.UserApplication;
import com.revature.rideforce.user.beans.User;
import com.revature.rideforce.user.beans.UserRole;
import com.sun.jersey.api.client.ClientResponse.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RegistrationKeyControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void loggedOutUserCannotGetAuthKey() throws Exception {
		this.mockMvc.perform(get("/registration-key")).andExpect(status().isForbidden());
	}
	
	@Test
	public void adminCanGetAuthKey() throws Exception{
		User user = new User(); 
		UserRole ur = new UserRole();
		ur.setType("ADMIN");
		user.setFirstName("admin");
		user.setLastName("revature");
		user.setRole(ur);
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
		
		
		this.mockMvc.perform(get("/registration-key")).andExpect(status().isOk());
	}
	
	@Test
	public void trainerCanGetAuthKey() throws Exception{
		User user = new User(); 
		UserRole ur = new UserRole();
		ur.setType("TRAINER");
		user.setFirstName("trainer");
		user.setLastName("revature");
		user.setRole(ur);
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
		
		
		this.mockMvc.perform(get("/registration-key")).andExpect(status().isOk());
	}
	
	@Test
	public void driverCantGetAuthKey() throws Exception{
		User user = new User(); 
		UserRole ur = new UserRole();
		ur.setType("DRIVER");
		user.setFirstName("driver");
		user.setLastName("revature");
		user.setRole(ur);
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
		
		
		this.mockMvc.perform(get("/registration-key")).andExpect(status().isForbidden());
	}
}
