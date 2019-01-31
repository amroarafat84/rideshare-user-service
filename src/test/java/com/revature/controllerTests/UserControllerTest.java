package com.revature.controllerTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.revature.rideforce.user.UserApplication;
import com.revature.rideforce.user.beans.User;
import com.revature.rideforce.user.beans.UserRegistrationInfo;
import com.revature.rideforce.user.beans.UserRole;
import com.revature.rideforce.user.controllers.UserController;
import com.revature.rideforce.user.repository.UserRepository;
import com.revature.rideforce.user.services.UserService;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
	
	// Current build allows anyone to get request all users, this should be changed to be more secure
	@Test
	public void loggedOutUserCanGetUsers() throws Exception {
		this.mockMvc.perform(get("/users")).andExpect(status().isOk());
	}
	
	@Test
	public void loggedOutUserCanGetUsersById() throws Exception {
		this.mockMvc.perform(get("/users/1")).andExpect(status().isOk());
	}
	
	@Test
	public void loggedOutUserCannotPostUsers() throws Exception {
		this.mockMvc.perform(post("/users")).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void incorrectFormatWillReturn400PostUsers() throws Exception {
		User admin = userRepository.findById(1); //unfortunately have to get a user
		UserRegistrationInfo info = new UserRegistrationInfo();
		info.setUser(admin);
		info.setPassword("password");
		info.setRegistrationKey("ThisIsAKey");
		ObjectMapper om = new ObjectMapper();
		String userRegInfo = om.writeValueAsString(info);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, "password", admin.getAuthorities()));
		this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(userRegInfo)).andExpect(status().is4xxClientError());
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void loggedOutUserCannotPostUsersById() throws Exception {
		this.mockMvc.perform(post("/users/1")).andExpect(status().isForbidden());
	}
	
	@Test
	public void loggedOutUserCannotPutUsers() throws Exception {
		this.mockMvc.perform(put("/users")).andExpect(status().isForbidden());
	}
	
	@Test
	public void loggedOutUserCannotPutUsersById() throws Exception {
		this.mockMvc.perform(put("/users/1")).andExpect(status().isForbidden());
	}
	
	@Test
	public void loggedOutUserCannotDeleteUsers() throws Exception {
		this.mockMvc.perform(delete("/users")).andExpect(status().isForbidden());
	}
	
	@Test
	public void loggedOutUserCannotDeleteUsersById() throws Exception {
		this.mockMvc.perform(delete("/users/1")).andExpect(status().isForbidden());
	}
	
	// Logged out user get requests permissions may be security concern
	@Test
	public void loggedOutUserCanGetByEmail() throws Exception {
		this.mockMvc.perform(get("/users?email=admin@revature.com")).andExpect(status().isOk());
	}
	
	@Test
	public void loggedOutUserCanGetByEmptyEmail() throws Exception {
		this.mockMvc.perform(get("/users?email=")).andExpect(status().isNotFound());
	}

	@Test
	public void loggedOutUserCanGetByInvalidEmail() throws Exception {
		this.mockMvc.perform(get("/users?email=test")).andExpect(status().isNotFound());
	}
	
	// Logged out user get requests permissions may be security concern
	@Test
	public void loggedOutUserCanGetByOfficeIdAndRole() throws Exception {
		log.trace("office = 1 role = ADMIN");
		this.mockMvc.perform(get("/users?office=1&role=ADMIN")).andExpect(status().isOk());
	}
	
	@Test
	public void loggedOutUserBadRequestByEmptyOfficeIdAndEmptyRole() throws Exception {
		log.trace("office = empty role = empty");
		this.mockMvc.perform(get("/users?office=&role=")).andExpect(status().isBadRequest());
	}
	
	@Test
	public void loggedOutUserNotFoundByValidOfficeIdAndEmptyRole() throws Exception {
		log.trace("office = 1 role = empty" );
		this.mockMvc.perform(get("/users?office=1&role=")).andExpect(status().isBadRequest());
	}
	
	@Test
	public void loggedOutUserNotFoundByEmptyOfficeIdAndvalideRole() throws Exception{
		log.trace("office = empty role = ADMIN" );
		this.mockMvc.perform(get("/users?office=&role=ADMIN")).andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void loggedOutUserNotFoundGetByInvalidId() throws Exception {
		this.mockMvc.perform(get("/users/-1")).andExpect(status().isNotFound());
	} 
	
	@Test
	public void loggedOutUserBadRequestGetByStringId() throws Exception {
		this.mockMvc.perform(get("/users/a")).andExpect(status().isBadRequest());
	}
	
	@Test
	public void loggedOutUserFindByWeirdCaseEmailShouldWork() throws Exception
	{ 							//so to put parameters in get request url, it's a ? not a /   !!!!
		this.mockMvc.perform(get("/users?email=adMIN@REVature.com")).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void loggedOutUserCantDeleteAccount() throws Exception
	{
		this.mockMvc.perform(delete("/users/1")).andExpect(status().isForbidden()); //should be forbidden since not logged in
			//throws PermissionDeniedException
	}
	
	@Transactional   //"mock" mvc still actually delete
	@Test
	public void loggedInAdminUserCanDeleteAccount() throws Exception
	{
		User admin = userRepository.findById(1); //unfortunately have to get a user
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, "password", admin.getAuthorities()));
		this.mockMvc.perform(delete("/users/1")).andExpect(status().is2xxSuccessful());
		SecurityContextHolder.getContext().setAuthentication(null);
		
	}
	
	@Transactional
	@Test
	public void loggedInUserCanSaveAccount() throws Exception{
		User user = userRepository.findById(1); 
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
		this.mockMvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\":\"fname\",\"lastName\":\"lname\",\"email\":\"email@email.com\",\"password\":\"password\"}"))
				.andExpect(status().is2xxSuccessful());
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	//This is to test out registration user currently gets 400 error
	@Test
	public void registerUserTest() throws Exception{
		log.trace("register user test");
		User user = new User();
		UserRole userR = new UserRole(); 
		userR.setType("DRIVER");
		user.setFirstName("john");
		user.setLastName("doe");
		user.setEmail("jdoe@email.com");
		user.setPassword("password");
		user.setRole(userR);
		UserRegistrationInfo ur = new UserRegistrationInfo(); 
		ur.setUser(user);
		ur.setPassword("password");
		ur.setRegistrationKey("mykey");
		
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter(); 
		
		String requestJson = ow.writeValueAsString(ur);
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
		log.trace("register user test before mock");
		this.mockMvc.perform(post("/users/registration").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
			.content(requestJson))
			.andExpect(status().isCreated());
		
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}