package com.einfochips.userservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.userservice.dtos.UserResponseDTO;
import com.einfochips.userservice.dtos.UserSearchRequestDTO;
import com.einfochips.userservice.enums.Role;
import com.einfochips.userservice.services.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
public class UserSearchController {
	private final UserSearchService searchUserService;
	private  final MessageService messageService;

	/**
	 * Search users with dynamic filtering and pagination.
	 *
	 * <p>
	 * Supports filtering by: - email (partial match) - role - createdBy, updatedBy
	 * - date range
	 * </p>
	 *
	 *
	 *
	 * @return Paginated list of users
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> searchUsers(
			@RequestParam(required = false) String email, @RequestParam(required = false) Role role,
			@RequestParam(required = false) Long createdById, @RequestParam(required = false) Long updatedById,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
			Pageable pageable) {

		UserSearchRequestDTO request = new UserSearchRequestDTO(email, role, createdById, updatedById, fromDate,
				toDate);

		Page<UserResponseDTO> response = searchUserService.searchUsers(request, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(response, messageService.getMessage(
						"user.fetch.success"
				)));
	}
}
