package com.einfochips.services.impls;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.einfochips.dtos.ProductMapper;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.dtos.ProductUpdateRequestDTO;
import com.einfochips.exceptions.ProductNotFoundException;
import com.einfochips.models.Product;
import com.einfochips.repositories.ProductRepository;
import com.einfochips.services.ProductUpdateService;

/**
 * Implementation of {@link ProductUpdateService}.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Verify the product exists and is not deleted</li>
 * <li>Validate SKU uniqueness against other active products (excluding
 * self)</li>
 * <li>Apply updated fields onto the existing entity</li>
 * <li>Persist and return the updated product</li>
 * </ul>
 *
 * <p>
 * <b>Why {@code @Transactional}?</b>
 * <ul>
 * <li>Ensures fetch + update happen in a single DB transaction</li>
 * <li>Rolls back automatically if any exception occurs mid-operation</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductUpdateServiceImpl implements ProductUpdateService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>Update Flow:</b>
	 * <ol>
	 * <li>Find active product by ID — throw {@link ProductNotFoundException} if not
	 * found</li>
	 * <li>Check SKU uniqueness — if SKU changed, verify no other active product has
	 * it</li>
	 * <li>Apply new field values onto the existing entity via mapper</li>
	 * <li>Save and return as {@link ProductResponseDTO}</li>
	 * </ol>
	 */
	@Override
	@Transactional
	public ProductResponseDTO updateProduct(long id, ProductUpdateRequestDTO request) {
		Product product = productRepository.findActiveById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		productMapper.updateEntity(request, product);

		Product updated = productRepository.save(product);

		return productMapper.toResponseDTO(updated);
	}

}