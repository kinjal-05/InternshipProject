package com.einfochips.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import com.einfochips.dtos.ApiResponse;
import com.einfochips.exceptions.ProductNotFoundException;

/**
 * Centralized exception handler for all REST controllers.
 *
 * <p>
 * Catches application-level and validation exceptions and returns consistent
 * {@link ApiResponse} error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// ── Validation errors (e.g. @NotBlank, @Size) ──────────────────────────

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String field = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(field, message);
		});

		log.warn("Validation failed: {}", errors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure("Validation failed"));
	}

	// ── Product not found ───────────────────────────────────────────────────

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleProductNotFound(ProductNotFoundException ex) {
		log.warn("Product not found: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.failure("An unexpected error occurred"));
	}

}