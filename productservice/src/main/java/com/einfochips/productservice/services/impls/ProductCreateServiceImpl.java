package com.einfochips.productservice.services.impls;

import com.einfochips.productservice.dtos.ProductMapper;
import com.einfochips.productservice.dtos.ProductRequestDTO;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.productservice.models.Product;
import com.einfochips.productservice.repositories.ProductRepository;
import com.einfochips.productservice.services.ProductCreateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for creating new products.
 * Handles the business logic for product creation operations.
 *
 * Dependencies are injected via constructor using @RequiredArgsConstructor (Lombok).
 */
@Service
@RequiredArgsConstructor
public class ProductCreateServiceImpl implements ProductCreateService {

	/**
	 * Repository for performing database operations on Product entities.
	 * Used to persist the newly created product.
	 */
	private final ProductRepository productRepository;

	/**
	 * Mapper for converting between Product DTOs and Product entities.
	 * Uses MapStruct or similar mapping framework to avoid manual mapping.
	 */
	private final ProductMapper productMapper;

	/**
	 * Creates a new product by:
	 * 1. Converting the incoming request DTO to a Product entity
	 * 2. Saving the entity to the database
	 * 3. Converting the saved entity back to a response DTO
	 *
	 * @Transactional ensures the entire operation is atomic —
	 * if saving fails, no partial data is committed to the database.
	 *
	 * @param requestDTO  the product data received from the API request
	 * @return ProductResponseDTO containing the saved product details
	 */
	@Override
	@Transactional
	public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {

		Product product = productMapper.toEntity(requestDTO);
		Product saved = productRepository.save(product);

		return productMapper.toResponseDTO(saved);
	}
}
