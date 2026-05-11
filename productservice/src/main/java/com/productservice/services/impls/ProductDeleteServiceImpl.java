package com.productservice.services.impls;

import com.utility.exceptions.ResourceNotFoundException;
import com.productservice.repositories.ProductRepository;
import com.productservice.services.ProductDeleteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProductDeleteService}.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Verify the product exists and is not already deleted</li>
 * <li>Soft-delete via a {@code @Modifying} JPQL query in repository</li>

 * </ul>
 *
 * <p>
 * <b>Why {@code @Modifying} JPQL instead of fetch + save?</b>
 * <ul>
 * <li>Single UPDATE query — no need to load the full entity into memory</li>
 * <li>More efficient for delete operations where no response data is
 * needed</li>
 * <li>Returns affected row count — used to detect if product existed</li>
 * </ul>
 *
 * <p>
 * <b>Why {@code @Transactional}?</b>
 * <ul>
 * <li>{@code @Modifying} queries require an active transaction</li>
 * <li>Rolls back automatically if any exception occurs</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDeleteServiceImpl implements ProductDeleteService {

	private final ProductRepository productRepository;

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>Delete Flow:</b>
	 * <ol>
	 * <li>Execute soft-delete JPQL UPDATE via {@code repository.delete(id)}</li>
	 * <li>Check affected rows — if 0, product does not exist or is already
	 * deleted</li>
	
	 * </ol>
	 */
	@Override
	@Transactional
	public void deleteProduct(long id) {
		int affectedRows = productRepository.delete(id);
		if (affectedRows == 0) {
			throw new ResourceNotFoundException("Product not found with ID: " + id);
		}
	}

}
