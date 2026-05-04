package com.einfochips.services.impls;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.einfochips.config.ProductSpecification;
import com.einfochips.dtos.ProductMapper;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.models.Product;
import com.einfochips.repositories.ProductRepository;
import com.einfochips.services.ProductSearchService;

/**
 * Implementation of {@link ProductSearchService} using JPA Specifications.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Build a dynamic {@link Specification} based on provided filters</li>
 * <li>Delegate paginated query to {@link ProductRepository}</li>
 * <li>Map results to {@link ProductResponseDTO} records and return</li>
 * </ul>
 *
 * <p>
 * <b>Why Specification over JPQL {@code @Query}?</b>
 * <ul>
 * <li>Type-safe — uses JPA Criteria API, no raw string field names</li>
 * <li>Composable — each filter is an independent predicate</li>
 * <li>Maintainable — adding a new filter only requires one new predicate in
 * {@link ProductSpecification}, no changes to this service or repository</li>
 * </ul>
 *
 * <p>
 * <b>Why {@code @Transactional(readOnly = true)}?</b>
 * <ul>
 * <li>Read-only operation — skips dirty checking and flush on commit</li>
 * <li>Improves performance for paginated fetch operations</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * <b>Search Flow:</b>
	 * <ol>
	 * <li>Build {@link Specification} from provided filters via
	 * {@link ProductSpecification#withFilters}</li>
	 * <li>Execute paginated query via
	 * {@code repository.findAll(spec, pageable)}</li>
	 * <li>Map each {@link Product} entity to {@link ProductResponseDTO} record</li>
	 * <li>Return paginated result</li>
	 * </ol>
	 */
	@Override
	@Transactional
	public Page<ProductResponseDTO> searchProducts(String search, String category, Pageable pageable) {
		Specification<Product> spec = ProductSpecification.withFilters(search, category);
		Page<Product> productPage = productRepository.findAll(spec, pageable);
		return productPage.map(productMapper::toResponseDTO);
	}

}
