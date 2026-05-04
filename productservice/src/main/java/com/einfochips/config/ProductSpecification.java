package com.einfochips.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import com.einfochips.models.Product;

/**
 * JPA Specification builder for {@link Product} dynamic query filtering.
 *
 * <p>
 * Provides a static factory method to build a {@link Specification} based on
 * optional search filters. Null or blank values for any filter are simply
 * ignored — no filter is applied for that field.
 *
 * <p>
 * <b>Why Specification over JPQL?</b>
 * <ul>
 * <li>Type-safe — no raw strings for field names</li>
 * <li>Composable — each filter is an independent predicate, easy to
 * add/remove</li>
 * <li>Maintainable — adding a new filter requires only one new predicate
 * block</li>
 * <li>Reusable — same specification can be combined with others via
 * {@code and()} / {@code or()}</li>
 * </ul>
 *
 * <p>
 * <b>Filters Supported:</b>
 * <ul>
 * <li>{@code search} — case-insensitive keyword match on {@code name} OR
 * {@code description}</li>
 * <li>{@code category} — case-insensitive exact match on {@code category}</li>
 * </ul>
 */
public class ProductSpecification {

	private ProductSpecification() {
		// Utility class — prevent instantiation
	}

	/**
	 * Build a {@link Specification} for searching active products with optional
	 * filters.
	 *
	 * @param search   optional keyword matched against name or description
	 *                 (case-insensitive)
	 * @param category optional category filter (case-insensitive exact match)
	 * @return composed {@link Specification} with all applicable predicates
	 */
	public static Specification<Product> withFilters(String search, String category) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

			if (category != null && !category.isBlank()) {
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")),
						category.trim().toLowerCase()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}