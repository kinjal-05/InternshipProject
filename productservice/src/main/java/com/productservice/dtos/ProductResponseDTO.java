// File: src/main/java/com/productservice/dtos/ProductResponseDTO.java

package com.productservice.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning Product data in API responses.
 *
 * Since Product now has ManyToOne relation with Category,
 * response should expose categoryId + categoryName.
 */
public record ProductResponseDTO(

		long id,
		String name,
		String description,

		Long categoryId,
		String categoryName,

		BigDecimal price,
		Integer stockQuantity,

		LocalDateTime createdAt,
		LocalDateTime updatedAt

) {
}