package com.einfochips.controllers;

import com.einfochips.dtos.UserApiResponse;
import com.einfochips.dtos.UserRequestDTO;
import com.einfochips.dtos.UserResponseDTO;
import com.einfochips.services.UserCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User-related operations.
 *
 * <p>
 * Responsibilities: - Handle incoming HTTP requests - Delegate business logic
 * to Service layer - Return standardized HTTP responses
 * </p>
 *
 * <p>
 * NOTE: - All validations are handled using @Valid (Bean Validation) -
 * Exception handling is centralized in GlobalExceptionHandler
 * </p>
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCreateController {
	private final UserCreateService createUserService;

	/**
	 * Register a new user.
	 *
	 * @param request User registration details (validated using @Valid)
	 * @return Created user response
	 *
	 *         HTTP Status: - 201 CREATED on success - 400 BAD REQUEST if validation
	 *         fails
	 */
	@PostMapping("/registerUser")
	public ResponseEntity<UserApiResponse<UserResponseDTO>> createUser(@RequestBody @Valid UserRequestDTO request) {

		UserResponseDTO response = createUserService.createUser(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(UserApiResponse.success(response, "User created successfully"));
	}
}
