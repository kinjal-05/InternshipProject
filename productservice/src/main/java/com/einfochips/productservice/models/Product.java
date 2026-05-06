package com.einfochips.productservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a Product in the system.
 *
 * <p>
 * This class maps to the {@code products} table and stores product information
 * along with auditing and soft-delete support.
 *
 * <p>
 * <b>Key Features:</b>
 * <ul>
 * <li><b>Unique Constraint:</b> Ensures that SKU is unique among active
 * products by combining {@code pm_sku} and {@code pm_deleted_timestamp}. This
 * allows reuse of the same SKU after soft deletion.</li>
 *
 * <li><b>Validation:</b> Uses Bean Validation annotations such as
 * {@code @NotBlank}, {@code @Size}, {@code @DecimalMin}, and {@code @Min} to
 * enforce data integrity at the API level.</li>
 *
 * <li><b>Auditing:</b> Integrated with {@link AuditingEntityListener} to
 * automatically populate:
 * <ul>
 * <li>{@code createdAt}, {@code updatedAt}</li>
 * <li>{@code createdById}, {@code updatedById}</li>
 * </ul>
 * </li>
 *
 * <li><b>Soft Delete:</b> Instead of physically deleting records, products are
 * marked as deleted using:
 * <ul>
 * <li>{@code isDeleted} flag</li>
 * <li>{@code deletedTimestamp}</li>
 * </ul>
 * This ensures data retention and auditability.</li>
 *
 * <li><b>Performance Optimization:</b>
 * <ul>
 * <li>{@code @DynamicInsert}: Includes only non-null fields in INSERT
 * queries</li>
 * <li>{@code @DynamicUpdate}: Updates only modified fields in UPDATE
 * queries</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <p>
 * <b>Database Columns Mapping:</b>
 * <ul>
 * <li>{@code pm_id} - Primary key</li>
 * <li>{@code pm_name} - Product name</li>
 * <li>{@code pm_description} - Product description</li>
 * <li>{@code pm_sku} - Stock Keeping Unit (unique with soft delete
 * support)</li>
 * <li>{@code pm_category} - Product category</li>
 * <li>{@code pm_price} - Product price</li>
 * <li>{@code pm_stock_quantity} - Available stock</li>
 * <li>{@code pm_created_at}, {@code pm_updated_at} - Audit timestamps</li>
 * <li>{@code pm_created_by}, {@code pm_updated_by} - Audit user references</li>
 * <li>{@code pm_is_deleted} - Soft delete flag</li>
 * <li>{@code pm_deleted_timestamp} - Soft delete timestamp</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> This entity relies on Spring Data JPA auditing configuration
 * (e.g., {@code @EnableJpaAuditing}) and a proper implementation of
 * {@code AuditorAware} to populate audit fields.
 */
@Entity
@Table(name = "products", uniqueConstraints = { @UniqueConstraint(columnNames = { "pm_id", "pm_deleted_timestamp" }) })
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pm_id", updatable = false, nullable = false)
	@EqualsAndHashCode.Include
	private long id;

	@NotBlank(message = "Product name is required")
	@Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
	@Column(name = "pm_name", nullable = false, length = 100)
	private String name;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	@Column(name = "pm_description", length = 500)
	private String description;

	@NotBlank(message = "Category is required")
	@Size(max = 50, message = "Category must not exceed 50 characters")
	@Column(name = "pm_category", nullable = false, length = 50)
	private String category;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
	@Digits(integer = 10, fraction = 2, message = "Price format is invalid")
	@Column(name = "pm_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@NotNull(message = "Stock quantity is required")
	@Min(value = 0, message = "Stock quantity cannot be negative")
	@Column(name = "pm_stock_quantity", nullable = false)
	private Integer stockQuantity;

	@CreationTimestamp
	@Column(name = "pm_created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "pm_updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder.Default
	@Column(name = "pm_is_deleted", nullable = false)
	private boolean isDeleted = false;

	@Column(name = "pm_deleted_timestamp")
	private LocalDateTime deletedTimestamp;

}