package com.einfochips.productservice.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for receiving Product data in API requests.
 *
 * <p>
 * Used as the request body for:
 * <ul>
 * <li>POST /api/v1/products — Create Product</li>
 * <li>PUT /api/v1/products/{id} — Update Product</li>
 * </ul>
 *
 * <p>
 * <b>Validation Rules:</b>
 * <ul>
 * <li>{@code name} — Required, 2–100 characters</li>
 * <li>{@code description} — Optional, max 500 characters</li>
 * <li>{@code sku} — Required, max 50 characters</li>
 * <li>{@code category} — Required, max 50 characters</li>
 * <li>{@code price} — Required, must be greater than 0, max 2 decimal
 * places</li>
 * <li>{@code stockQuantity} — Required, cannot be negative</li>
 * </ul>
 *
 * <p>
 * NOTE: This DTO is validated at the Controller layer using {@code @Valid}.
 * Validation errors are handled centrally by {@code GlobalExceptionHandler}.
 */

public record ProductRequestDTO(

		@NotBlank(message = "Product name is required") @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters") String name,

		@Size(max = 500, message = "Description must not exceed 500 characters") String description,

		@NotBlank(message = "Category is required") @Size(max = 50, message = "Category must not exceed 50 characters") String category,

		@NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") @Digits(integer = 10, fraction = 2, message = "Price format is invalid") BigDecimal price,

		@NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity cannot be negative") Integer stockQuantity) {
}