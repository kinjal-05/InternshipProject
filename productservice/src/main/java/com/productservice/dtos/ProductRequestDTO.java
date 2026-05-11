// File: src/main/java/com/productservice/dtos/ProductRequestDTO.java

package com.productservice.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for Create / Update Product Request
 *
 * Since Product now uses ManyToOne relation with Category,
 * request should receive categoryId instead of category name.
 */
public record ProductRequestDTO(

		@NotBlank(message = "Product name is required")
		@Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
		String name,

		@Size(max = 500, message = "Description must not exceed 500 characters")
		String description,

		@NotNull(message = "Category is required")
		Long categoryId,

		@NotNull(message = "Price is required")
		@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
		@Digits(integer = 10, fraction = 2, message = "Price format is invalid")
		BigDecimal price,

		@NotNull(message = "Stock quantity is required")
		@Min(value = 0, message = "Stock quantity cannot be negative")
		Integer stockQuantity

) {
}