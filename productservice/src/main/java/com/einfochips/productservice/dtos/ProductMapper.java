package com.einfochips.productservice.dtos;

import com.einfochips.productservice.models.Product;
import org.springframework.stereotype.Component;



@Component
public class ProductMapper {

	/**
	 * Map {@link ProductRequestDTO} to a new {@link Product} entity.
	 *
	 * @param dto the incoming request data
	 * @return a new Product entity (without audit/id fields)
	 */
	public Product toEntity(ProductRequestDTO dto) {
		return Product.builder().name(dto.name()).description(dto.description()).category(dto.category())
				.price(dto.price()).stockQuantity(dto.stockQuantity()).build();
	}

	public ProductResponseDTO toResponseDTO(Product product) {
		return new ProductResponseDTO(product.getId(), product.getName(), product.getDescription(),
				product.getCategory(), product.getPrice(), product.getStockQuantity(), product.getCreatedAt(),
				product.getUpdatedAt());
	}

	/**
	 * Apply updated fields from {@link ProductRequestDTO} onto an existing
	 * {@link Product} entity. Used during product update so that audit fields
	 * (createdAt, createdById) are preserved.
	 *
	 * @param dto    the update request data
	 * @param entity the existing entity to update
	 */
	public void updateEntity(ProductUpdateRequestDTO dto, Product entity) {
		if (dto.name() != null) {
			entity.setName(dto.name());
		}

		if (dto.description() != null) {
			entity.setDescription(dto.description());
		}

		if (dto.category() != null) {
			entity.setCategory(dto.category());
		}

		if (dto.price() != null) {
			entity.setPrice(dto.price());
		}

		if (dto.stockQuantity() != null) {
			entity.setStockQuantity(dto.stockQuantity());
		}
	}
}
