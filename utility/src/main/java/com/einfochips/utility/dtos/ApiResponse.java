package com.einfochips.utility.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for standardizing all HTTP responses.
 *
 * <p>
 * This class provides a consistent structure for API responses across the
 * application. It encapsulates the success status, message, payload data, and
 * timestamp of the response.
 * </p>
 *
 * <p>
 * <b>Response Structure:</b>
 * <ul>
 * <li>{@code success} - Indicates whether the request was successful.</li>
 * <li>{@code message} - Descriptive message about the response.</li>
 * <li>{@code data} - Generic payload containing response data (if any).</li>
 * <li>{@code timestamp} - Time at which the response was generated.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Usage:</b>
 * <ul>
 * <li>Use {@link #success(Object, String)} for successful responses.</li>
 * <li>Use {@link #failure(String)} for error responses.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This approach ensures consistency in API design, simplifies client-side
 * parsing, and improves maintainability of the application.
 * </p>
 *
 * @param <T> the type of the response payload
 */
public record ApiResponse<T>(
		boolean success,
		String message,
		T data,
		LocalDateTime timestamp
) {

	/**
	 * Creates a successful API response.
	 *
	 * @param data    the response payload
	 * @param message the success message
	 * @param <T>     the type of the response payload
	 * @return populated ApiResponse with success status
	 */
	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(
				true,
				message,
				data,
				LocalDateTime.now()
		);
	}

	/**
	 * Creates a failure API response.
	 *
	 * @param message the error message
	 * @param <T>     the type of response payload
	 * @return populated ApiResponse with failure status
	 */
	public static <T> ApiResponse<T> failure(String message) {
		return new ApiResponse<>(
				false,
				message,
				null,
				LocalDateTime.now()
		);
	}
}