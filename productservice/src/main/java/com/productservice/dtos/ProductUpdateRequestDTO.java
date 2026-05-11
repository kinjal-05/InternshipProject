package com.productservice.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductUpdateRequestDTO(

		@Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
		String name,

		@Size(max = 500, message = "Description must not exceed 500 characters")
		String description,

		Long categoryId,

		@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
		@Digits(integer = 10, fraction = 2, message = "Price format is invalid")
		BigDecimal price,

		@Min(value = 0, message = "Stock quantity cannot be negative")
		Integer stockQuantity

) {}