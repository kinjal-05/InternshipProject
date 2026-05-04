package com.einfochips.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.einfochips.dtos.ApiResponse;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.services.ProductGetByIdService;

/**
 * REST Controller for Get Product by ID operation.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Handle incoming HTTP GET request for fetching a single product</li>
 * <li>Delegate business logic to Service layer</li>
 * <li>Return standardized HTTP response</li>
 * </ul>
 *
 * <p>
 * NOTE:
 * <ul>
 * <li>Only active (non-deleted) products are returned</li>
 * <li>Exception handling is centralized in {@code GlobalExceptionHandler}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductGetByIdController {

	private final ProductGetByIdService productGetByIdService;

	/**
	 * Get a single active product by its ID.
	 *
	 * @param id ID of the product to fetch
	 * @return product response
	 *
	 *         <p>
	 *         HTTP Status:
	 *         <ul>
	 *         <li>200 OK on success</li>
	 *         <li>404 NOT FOUND if product does not exist or is deleted</li>
	 *         </ul>
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable long id) {

		ProductResponseDTO response = productGetByIdService.getProductById(id);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response, "Product fetched successfully"));
	}

}
