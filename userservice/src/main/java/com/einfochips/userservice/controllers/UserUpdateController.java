package com.einfochips.userservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.userservice.dtos.UserResponseDTO;
import com.einfochips.userservice.dtos.UserUpdateRequestDTO;
import com.einfochips.userservice.services.UserUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class UserUpdateController {
	private final UserUpdateService updateUserService;
	private final MessageService messageService;

	/**
	 * Update user details.
	 *
	 * @param id      User ID
	 * @param request Fields to update
	 *
	 * @return Updated user details
	 *
	 *         HTTP Status: - 200 OK on success - 404 NOT FOUND if user does not
	 *         exist
	 */
	@PatchMapping("/updateUser/{id}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id,
	                                                               @RequestBody UserUpdateRequestDTO request) {

		UserResponseDTO updatedUser = updateUserService.updateUser(id, request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(updatedUser, messageService.getMessage(
						"user.update.success"
				)));
	}
}

