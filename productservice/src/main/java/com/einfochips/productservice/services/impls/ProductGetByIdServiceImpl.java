package com.einfochips.productservice.services.impls;

import com.einfochips.productservice.dtos.ProductMapper;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.utility.exceptions.ResourceNotFoundException;
import com.einfochips.productservice.models.Product;
import com.einfochips.productservice.repositories.ProductRepository;
import com.einfochips.productservice.services.ProductGetByIdService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProductGetByIdService}.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Fetch a single active product by its primary key</li>

 * <li>Map entity to response DTO and return</li>
 * </ul>
 *
 * <p>
 * <b>Why {@code @Transactional(readOnly = true)}?</b>
 * <ul>
 * <li>Marks the transaction as read-only — no dirty checking or flush on
 * commit</li>
 * <li>Improves performance for fetch-only operations</li>
 * <li>Prevents accidental writes inside this method</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductGetByIdServiceImpl implements ProductGetByIdService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>Fetch Flow:</b>
	 * <ol>

	 * found or deleted</li>
	 * <li>Map entity to {@link ProductResponseDTO} record and return</li>
	 * </ol>
	 */
	@Override
	@Transactional
	public ProductResponseDTO getProductById(long id) {

		Product product = productRepository.findActiveById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

		return productMapper.toResponseDTO(product);
	}

}
