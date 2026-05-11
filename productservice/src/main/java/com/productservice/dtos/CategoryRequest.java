package com.productservice.dtos;
import jakarta.validation.constraints.*;
public record CategoryRequest(

		@NotBlank(message = "Category name is required")
		@Size(max = 30, message = "Category name must not exceed 30 characters")
		String name,

		@NotNull(message = "Display order is required")
		@Min(value = 1, message = "Display order must be at least 1")
		@Max(value = 100, message = "Display order must not exceed 100")
		Integer displayOrder

) {
}
