package com.einfochips.productservice.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning Product data in API responses.
 *
 * <p>
 * Used as the response body for:
 * <ul>
 * <li>POST /api/v1/products — Create Product</li>
 * <li>GET /api/v1/products — List / Search Products</li>
 * <li>GET /api/v1/products/{id} — Get Product by ID</li>
 * <li>PUT /api/v1/products/{id} — Update Product</li>
 * </ul>
 *
 * <p>
 * <b>Security Note:</b>
 * <ul>
 * <li>Internal fields {@code isDeleted} and {@code deletedTimestamp} are
 * intentionally excluded to avoid exposing soft-delete internals in API
 * responses.</li>
 * </ul>
 *
 * <p>
 * <b>Audit Fields:</b>
 * <ul>
 * <li>{@code createdAt} — Timestamp when the product was created</li>
 * <li>{@code updatedAt} — Timestamp of the last update</li>
 * <li>{@code createdById} — ID of the user who created the product</li>
 * <li>{@code updatedById} — ID of the user who last updated the product</li>
 * </ul>
 *
 * <p>
 * NOTE: No validation annotations are needed here as this DTO is only used for
 * outgoing responses, never for input.
 */
public record ProductResponseDTO(

		long id, String name, String description, String category, BigDecimal price, Integer stockQuantity,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
}