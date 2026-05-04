package com.einfochips.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.einfochips.models.Product;

/**
 * Repository for {@link Product} entity.
 *
 * <p>
 * All queries use <b>JPQL</b> (not native SQL) to ensure correct field mapping.
 *
 * <p>
 * <b>Why JPQL over Native SQL?</b>
 * <ul>
 * <li>Native SQL returns raw column names (e.g., {@code pm_is_deleted}) which
 * Hibernate may fail to silently map to entity fields (e.g.,
 * {@code isDeleted}), returning {@code Optional.empty()} instead of throwing an
 * error.</li>
 * <li>JPQL uses entity field names directly — Hibernate handles column mapping
 * automatically and correctly.</li>
 * </ul>
 *
 * <p>
 * <b>Soft Delete Strategy:</b>
 * <ul>
 * <li>Products are never physically deleted from the database.</li>
 * <li>{@code delete()} sets {@code isDeleted = true} and records
 * {@code deletedTimestamp} via a {@code @Modifying} JPQL update.</li>
 * <li>All business-logic queries filter by {@code isDeleted = false} to return
 * only active products.</li>
 * </ul>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	/**
	 * Find an active (non-deleted) product by its primary key. Use this in
	 * ProductServiceImpl for all fetch-by-ID operations.
	 *
	 * @param id product primary key
	 * @return active product wrapped in Optional, or empty if not found / deleted
	 */
	@Query("SELECT p FROM Product p WHERE p.id = :id AND p.isDeleted = false")
	Optional<Product> findActiveById(@Param("id") long id);

	/**
	 * Search active products with optional keyword and category filters.
	 *
	 * <p>
	 * Passing {@code null} for any filter disables that filter entirely (the
	 * {@code IS NULL} check makes JPQL ignore it).
	 *
	 * @param search   optional keyword matched against name or description
	 *                 (case-insensitive)
	 * @param category optional category filter (case-insensitive exact match)
	 * @param pageable pagination and sorting configuration
	 * @return paginated list of matching active products
	 */
	@Query("""
			SELECT p FROM Product p
			WHERE p.isDeleted = false
			AND (:search   IS NULL OR LOWER(p.name)        LIKE LOWER(CONCAT('%', :search,   '%'))
			                       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search,   '%')))
			AND (:category IS NULL OR LOWER(p.category)    = LOWER(:category))
			""")
	Page<Product> searchProducts(@Param("search") String search, @Param("category") String category, Pageable pageable);

	/**
	 * Soft-delete a product by ID.
	 *
	 * <p>
	 * Sets {@code isDeleted = true} and records {@code deletedTimestamp} without
	 * physically removing the row from the database.
	 *
	 * @param id product primary key
	 * @return number of rows affected (1 if deleted, 0 if not found)
	 */
	@Modifying
	@Query("UPDATE Product p SET p.isDeleted = true, p.deletedTimestamp = CURRENT_TIMESTAMP WHERE p.id = :id")
	int delete(@Param("id") long id);

}