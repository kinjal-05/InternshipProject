package com.einfochips.controllers;

import com.einfochips.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.einfochips.dtos.ProductRequestDTO;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.services.ProductCreateService;

/**
 * REST Controller for Product Create operation.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Handle incoming HTTP POST request for product creation</li>
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
public class ProductCreateController {

	private final ProductCreateService productCreateService;

	/**
	 * Create a new product.
	 *
	 * @param request Product creation details (validated using {@code @Valid})
	 * @return Created product response
	 *
	 *         <p>
	 *         HTTP Status:
	 *         <ul>
	 *         <li>201 CREATED on success</li>
	 *         <li>400 BAD REQUEST if validation fails</li>
	 *         <li>409 CONFLICT if SKU already exists</li>
	 *         </ul>
	 */
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
			@RequestBody @Valid ProductRequestDTO request) {

		ProductResponseDTO response = productCreateService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(response, "Product created successfully"));
	}

}