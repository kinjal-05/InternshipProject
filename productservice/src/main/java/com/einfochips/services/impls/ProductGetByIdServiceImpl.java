package com.einfochips.services.impls;

import com.einfochips.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.einfochips.dtos.ProductMapper;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.models.Product;
import com.einfochips.repositories.ProductRepository;
import com.einfochips.services.ProductGetByIdService;

/**
 * Implementation of {@link ProductGetByIdService}.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Fetch a single active product by its primary key</li>
 * <li>Throw {@link ProductNotFoundException} if not found or soft-deleted</li>
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
	 * <li>Find active product by ID — throw {@link ProductNotFoundException} if not
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
