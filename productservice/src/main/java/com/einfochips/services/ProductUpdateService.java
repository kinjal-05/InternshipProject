package com.einfochips.services;

import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.dtos.ProductUpdateRequestDTO;

/**
 * Service interface for Product Update operation.
 *
 * <p>
 * Defines the contract for updating an existing active product.
 */
public interface ProductUpdateService {

	/**
	 * Update an existing active product by ID.
	 *
	 * @param id      ID of the product to update
	 * @param request updated product data
	 * @return updated product as {@link ProductResponseDTO}
	 */
	ProductResponseDTO updateProduct(long id, ProductUpdateRequestDTO request);

}
