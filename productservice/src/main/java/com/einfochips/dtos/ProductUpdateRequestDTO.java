package com.einfochips.dtos;

import java.math.BigDecimal;

public record ProductUpdateRequestDTO(

		String name, String description, String category, BigDecimal price, Integer stockQuantity) {
}