package com.productservice.services;

import com.productservice.dtos.ProductRequestDTO;
import com.productservice.dtos.ProductResponseDTO;

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
