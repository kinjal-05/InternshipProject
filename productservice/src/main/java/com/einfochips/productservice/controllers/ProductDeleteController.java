package com.einfochips.productservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.productservice.services.ProductDeleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Product Delete operation.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Handle incoming HTTP DELETE request for soft-deleting a product</li>
 * <li>Delegate business logic to Service layer</li>
 * <li>Return standardized HTTP response</li>
 * </ul>
 *
 * <p>
 * NOTE:
 * <ul>
 * <li>This is a soft delete — product is NOT physically removed from DB</li>
 * <li>Exception handling is centralized in {@code GlobalExceptionHandler}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductDeleteController {

	private final ProductDeleteService productDeleteService;
	private final MessageService messageService;

	/**
	 * Soft-delete a product by its ID.
	 *
	 * @param id ID of the product to delete
	 * @return success message response
	 *
	 *         <p>
	 *         HTTP Status:
	 *         <ul>
	 *         <li>200 OK on success</li>
	 *         <li>404 NOT FOUND if product does not exist or is already
	 *         deleted</li>
	 *         </ul>
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable long id) {

		productDeleteService.deleteProduct(id);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null, messageService.getMessage(
				"product.delete.success"
		)));
	}

}