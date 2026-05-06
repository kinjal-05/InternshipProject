package com.einfochips.productservice.services;

import com.einfochips.productservice.dtos.ProductRequestDTO;
import com.einfochips.productservice.dtos.ProductResponseDTO;

/**
 * Service interface defining the contract for Product CRUD operations.
 */
public interface ProductCreateService {

	/**
	 * Create a new product.
	 *
	 * @param requestDTO product data
	 * @return created product as response DTO
	 */
	ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
}
