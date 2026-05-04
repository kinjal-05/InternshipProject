package com.einfochips.services;

import com.einfochips.dtos.ProductResponseDTO;

/**
 * Service interface for Get Product by ID operation.
 *
 * <p>
 * Defines the contract for fetching a single active product by its primary key.
 */
public interface ProductGetByIdService {

	/**
	 * Fetch a single active (non-deleted) product by its ID.
	 *
	 * @param id ID of the product to fetch
	 * @return product as {@link ProductResponseDTO}
	 */
	ProductResponseDTO getProductById(long id);

}