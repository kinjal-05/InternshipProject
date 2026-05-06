package com.einfochips.userservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.userservice.dtos.UserRequestDTO;
import com.einfochips.userservice.dtos.UserResponseDTO;
import com.einfochips.userservice.services.UserCreateService;
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
	private  final MessageService messageService;
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

	public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody @Valid UserRequestDTO request) {

		UserResponseDTO response = createUserService.createUser(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(response,
						messageService.getMessage(
								"user.create.success"
						)
				));
	}
}
