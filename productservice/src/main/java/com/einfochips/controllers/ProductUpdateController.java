package com.einfochips.controllers;

import com.einfochips.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.dtos.ProductUpdateRequestDTO;
import com.einfochips.services.ProductUpdateService;

/**
 * REST Controller for Product Update operation.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Handle incoming HTTP PUT request for product update</li>
 * <li>Delegate business logic to Service layer</li>
 * <li>Return standardized HTTP response</li>
 * </ul>
 *
 * <p>
 * NOTE:
 * <ul>
 * <li>All validations are handled using {@code @Valid} (Bean Validation)</li>
 * <li>Exception handling is centralized in {@code GlobalExceptionHandler}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductUpdateController {

	private final ProductUpdateService productUpdateService;

	/**
	 * Update an existing product by ID.
	 *
	 * @param id      ID of the product to update
	 * @param request Updated product details (validated using {@code @Valid})
	 * @return Updated product response
	 *
	 *         <p>
	 *         HTTP Status:
	 *         <ul>
	 *         <li>200 OK on success</li>
	 *         <li>400 BAD REQUEST if validation fails</li>
	 *         <li>404 NOT FOUND if product does not exist or is deleted</li>
	 *         <li>409 CONFLICT if updated SKU already exists on another
	 *         product</li>
	 *         </ul>
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable long id,
	                                                                     @RequestBody @Valid ProductUpdateRequestDTO request) {
		ProductResponseDTO response = productUpdateService.updateProduct(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response, "Product updated successfully"));
	}

}
