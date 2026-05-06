package com.einfochips.productservice.services.impls;

import com.einfochips.productservice.dtos.ProductMapper;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.productservice.dtos.ProductUpdateRequestDTO;
import com.einfochips.utility.exceptions.ResourceNotFoundException;
import com.einfochips.productservice.models.Product;
import com.einfochips.productservice.repositories.ProductRepository;
import com.einfochips.productservice.services.ProductUpdateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

		productMapper.updateEntity(request, product);

		Product updated = productRepository.save(product);

		return productMapper.toResponseDTO(updated);
	}

}