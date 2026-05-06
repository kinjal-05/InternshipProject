package com.einfochips.productservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.productservice.dtos.ProductUpdateRequestDTO;
import com.einfochips.productservice.services.ProductUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	private  final MessageService messageService;

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
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response, messageService.getMessage(
				"product.update.success"
		)));
	}

}
