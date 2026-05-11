package com.productservice.services.impls;


import com.productservice.dtos.ProductMapper;
import com.productservice.dtos.ProductRequestDTO;
import com.productservice.dtos.ProductResponseDTO;

import com.productservice.models.Category;
import com.productservice.models.Product;
import com.productservice.repositories.CategoryRepository;
import com.productservice.repositories.ProductRepository;
import com.productservice.services.ProductCreateService;
import com.utility.exceptions.ResourceAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCreateServiceImpl implements ProductCreateService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ProductMapper productMapper;

	@Override
	@Transactional
	public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {

		Category category = categoryRepository
				.findByIdAndIsDeletedFalse(requestDTO.categoryId())
				.orElseThrow(() -> new EntityNotFoundException(
						"Category not found with id: " + requestDTO.categoryId()
				));


		Product product = productMapper.toEntity(requestDTO);
		product.setCategory(category);

		try {
			return productMapper.toResponseDTO(productRepository.save(product));
		} catch (DataIntegrityViolationException ex) {
			throw new ResourceAlreadyExistsException(
					"Product with SKU '" + requestDTO.categoryId() + "' already exists"
			);
		}
	}


}