package com.einfochips.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.einfochips.dtos.ProductResponseDTO;

/**
 * Service interface for Product Search / List operation.
 *
 * <p>
 * Defines the contract for searching active products with optional filters and
 * pagination support.
 */
public interface ProductSearchService {

	/**
	 * Search active products with optional keyword and category filters.
	 *
	 * @param search   optional keyword matched against name or description
	 *                 (case-insensitive)
	 * @param category optional category filter (case-insensitive exact match)
	 * @param pageable pagination and sorting configuration
	 * @return paginated list of matching active products as
	 *         {@link ProductResponseDTO}
	 */
	Page<ProductResponseDTO> searchProducts(String search, String category, Pageable pageable);

}