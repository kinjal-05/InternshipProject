package com.productservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"cat_name","cat_deleted_timestamp"}) // category name must be unique
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@DynamicInsert
@DynamicUpdate
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cat_id", updatable = false, nullable = false)
	private long id;

	@NotBlank(message = "Category name is required")
	@Size(max = 30, message = "Category name must not exceed 30 characters")
	@Column(name = "cat_name", nullable = false, length = 30, unique = true)
	private String name;

	@NotNull(message = "Display order is required")
	@Min(value = 1, message = "Display order must be at least 1")
	@Max(value = 100, message = "Display order must not exceed 100")
	@Column(name = "cat_display_order", nullable = false)
	private Integer displayOrder;

	/**
	 * References the User in userservice who owns this category.
	 * We store only the ID since User lives in a separate service/DB.
	 */
	@NotNull(message = "Owner is required")
	@Column(name = "cat_owner_id", nullable = false)
	private long ownerId;

	@JsonIgnore
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products;

	@Builder.Default
	@Column(name = "cat_is_deleted", nullable = false)
	private boolean isDeleted = false;

	@Column(name = "cat_deleted_timestamp")
	private LocalDateTime deletedTimestamp;
}
