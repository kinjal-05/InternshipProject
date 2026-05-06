package com.einfochips.productservice.controllers;

import com.einfochips.utility.config.MessageService;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.productservice.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Product Search / List operation.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Handle incoming HTTP GET request for searching and listing products</li>
 * <li>Accept optional query parameters for filtering and pagination</li>
 * <li>Delegate business logic to Service layer</li>
 * <li>Return standardized paginated HTTP response</li>
 * </ul>
 *
 * <p>
 * NOTE:
 * <ul>
 * <li>Only active (non-deleted) products are returned</li>
 * <li>All filters are optional — omitting them returns all active products</li>
 * <li>Exception handling is centralized in {@code GlobalExceptionHandler}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductSearchController {

	private final ProductSearchService productSearchService;

	private  final MessageService messageService;
	/**
	 * Search and list active products with optional filters and pagination.
	 *
	 * @param search   optional keyword matched against name or description
	 * @param category optional category filter (exact match, case-insensitive)
	 * @param pageable pagination and sorting (default: page=0, size=10, sort by
	 *                 createdAt DESC)
	 * @return paginated list of matching products
	 *
	 *         <p>
	 *         HTTP Status:
	 *         <ul>
	 *         <li>200 OK always (empty page returned if no results found)</li>
	 *         </ul>
	 *
	 *         <p>
	 *         Example requests:
	 *         <ul>
	 *         <li>GET /api/v1/products — all active products</li>
	 *         <li>GET /api/v1/products?search=bag — name/description contains
	 *         "bag"</li>
	 *         <li>GET /api/v1/products?category=electronics — category =
	 *         "electronics"</li>
	 *         <li>GET /api/v1/products?search=bag&category=kids — combined
	 *         filter</li>
	 *         <li>GET /api/v1/products?page=1&size=5 — pagination</li>
	 *         </ul>
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> searchProducts(
			@RequestParam(required = false) String search, @RequestParam(required = false) String category,
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<ProductResponseDTO> response = productSearchService.searchProducts(search, category, pageable);
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.success(response, messageService.getMessage(
						"product.fetch.success"
				)));
	}

}
