package com.productservice.services.impls;


import com.productservice.dtos.ProductMapper;
import com.productservice.dtos.ProductResponseDTO;
import com.productservice.dtos.ProductUpdateRequestDTO;

import com.productservice.models.Category;
import com.productservice.models.Product;
import com.productservice.repositories.CategoryRepository;
import com.productservice.repositories.ProductRepository;
import com.productservice.services.ProductUpdateService;
import com.utility.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ CORRECT

@Service
@RequiredArgsConstructor
public class ProductUpdateServiceImpl implements ProductUpdateService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ProductMapper productMapper;

	@Override
	@Transactional
	public ProductResponseDTO updateProduct(long id, ProductUpdateRequestDTO request) {

		// 1. Fetch product — fails if not found or soft deleted
		Product product = productRepository.findActiveById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Product not found with ID: " + id
				));



		// 3. If categoryId is being changed, validate and assign new category
		if (request.categoryId() != null) {
			Category category = categoryRepository
					.findByIdAndIsDeletedFalse(request.categoryId())
					.orElseThrow(() -> new EntityNotFoundException(
							"Category not found with id: " + request.categoryId()
					));



			product.setCategory(category);
		}

		// 4. Apply updated fields onto existing entity
		productMapper.updateEntity(request, product);

		// 5. Persist and return
		return productMapper.toResponseDTO(productRepository.save(product));
	}


}