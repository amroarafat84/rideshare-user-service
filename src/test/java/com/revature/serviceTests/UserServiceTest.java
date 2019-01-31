package com.revature.serviceTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.revature.rideforce.user.UserApplication;
import com.revature.rideforce.user.beans.User;
import com.revature.rideforce.user.beans.UserRegistrationInfo;
import com.revature.rideforce.user.exceptions.EmptyPasswordException;
import com.revature.rideforce.user.repository.UserRepository;
import com.revature.rideforce.user.services.UserService;

import javax.transaction.Transactional;

import org.junit.Assert;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserServiceTest {
	@TestConfiguration
	static class UserServiceTestContextConfiguration{
		@Bean
		public UserService userService(UserRepository userRepository) {
			return new UserService(userRepository);
		}
		
		
	}
	
	@InjectMocks
	@Autowired
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
//	@Before
//	public void setup() throws EmptyPasswordException {		
//		Mockito.when(userRepository.findById(u.getId())).thenReturn(u);
//	}
	
	@Test
	public void getUserByEmailTest() throws Exception{
		User uTest = new User(); 	//Testing
		User uResult = new User();	//Result
		UserRegistrationInfo ur = new UserRegistrationInfo();
		uTest.setFirstName("John");
		uTest.setLastName("Doe");
		uTest.setAddress("123 Address RD");
		uTest.setEmail("jdoe@email.com");
		uTest.setId(1);
		ur.setUser(uTest);
		ur.setPassword("password");
		ur.setRegistrationKey("key");
		uTest.setPassword(ur.getPassword());
		Mockito.doReturn(uResult).when(Mockito.spy(UserService.class).findByEmail("jdoe@email.com"));
		Assert.assertEquals(uTest.getUsername(), uResult.getUsername());
		
//		Mockito.doReturn(uTest).when(Mockito.spy(UserService.class).findByEmail("jdoe@email.com"));
//		Assert.assertEquals("jdoe@email.com", uTest.getUsername());
	}
	
}
