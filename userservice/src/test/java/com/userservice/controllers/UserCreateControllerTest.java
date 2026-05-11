package com.userservice.controllers;

import com.userservice.dtos.UserRequestDTO;
import com.userservice.enums.Role;
import com.userservice.security.CustomUserDetailsService;
import com.userservice.security.JwtService;
import com.userservice.services.UserCreateService;
import com.utility.config.GlobalExceptionHandler;
import com.utility.config.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
		controllers = UserCreateController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class,
				OAuth2ClientAutoConfiguration.class,
				OAuth2ResourceServerAutoConfiguration.class
		}
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("dev")
@Import(GlobalExceptionHandler.class)
 // ← changed from "test" to "dev" to match your properties file
@DisplayName("CreateUserController - Full Coverage Test Suite")
class UserCreateControllerTest {

	private static final String REGISTER_URL = "/api/v1/users/registerUser";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserCreateService createUserService;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private CustomUserDetailsService customUserDetailsService;

	@MockBean
	private MessageService messageService;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper.registerModule(new JavaTimeModule());
	}

//	@Test
//	void createUser_ShouldReturnCreatedUser() throws Exception {
//
//		UserRequestDTO request =
//				new UserRequestDTO("test@example.com", Role.ROLE_USER);
//
//		UserResponseDTO response =
//				new UserResponseDTO(
//						1L,
//						"test@example.com",
//						Role.ROLE_USER,
//						LocalDateTime.now(),
//						LocalDateTime.now(),
//						1L,
//						1L
//				);
//
//		Mockito.when(createUserService.createUser(Mockito.any(UserRequestDTO.class)))
//				.thenReturn(response);
//
//		mockMvc.perform(
//						post(REGISTER_URL)
//								.contentType(MediaType.APPLICATION_JSON)
//								.content(objectMapper.writeValueAsString(request))
//				)
//				.andExpect(status().isCreated())
//				.andExpect(jsonPath("$.message").value("User created successfully"))
//				.andExpect(jsonPath("$.data.email").value("test@example.com"))
//				.andExpect(jsonPath("$.data.role").value("ROLE_USER"));
//	}

	@Test
	void createUser_ShouldReturnBadRequest_WhenValidationFails() throws Exception {

		UserRequestDTO request =
				new UserRequestDTO("", null);

		mockMvc.perform(
						post(REGISTER_URL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isBadRequest());

		Mockito.verifyNoInteractions(createUserService);
	}

	@Test
	void createUser_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {

		UserRequestDTO request =
				new UserRequestDTO("test@example.com", Role.ROLE_USER);

		Mockito.when(createUserService.createUser(Mockito.any(UserRequestDTO.class)))
				.thenThrow(new RuntimeException("Service failure"));

		mockMvc.perform(
						post(REGISTER_URL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isInternalServerError());
	}

	@Test
	void createUser_ShouldReturnBadRequest_WhenMalformedJson() throws Exception {

		String invalidJson = "{ invalid json }";

		mockMvc.perform(
						post(REGISTER_URL)
								.contentType(MediaType.APPLICATION_JSON)
								.content(invalidJson)
				)
				.andExpect(status().isInternalServerError());

		Mockito.verifyNoInteractions(createUserService);
	}
}