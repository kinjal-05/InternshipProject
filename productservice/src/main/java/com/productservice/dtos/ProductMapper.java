package com.productservice.dtos;

import com.productservice.models.Category;
import com.productservice.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

	public Product toEntity(ProductRequestDTO dto) {
		return Product.builder()
				.name(dto.name())
				.description(dto.description())
				.price(dto.price())
				.stockQuantity(dto.stockQuantity())
				.build();
	}

	public ProductResponseDTO toResponseDTO(Product product) {
		Category category = product.getCategory();
		return new ProductResponseDTO(
				product.getId(),
				product.getName(),
				product.getDescription(),
				category.getId(),
				category.getName(),
				product.getPrice(),
				product.getStockQuantity(),
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}

	public void updateEntity(ProductUpdateRequestDTO dto, Product entity) {
		if (dto.name() != null)         entity.setName(dto.name());
		if (dto.description() != null)  entity.setDescription(dto.description());
		if (dto.price() != null)        entity.setPrice(dto.price());
		if (dto.stockQuantity() != null) entity.setStockQuantity(dto.stockQuantity());
	}
}