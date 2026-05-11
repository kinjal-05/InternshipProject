package com.productservice.dtos;

import java.time.LocalDateTime;

public record CategoryResponse(

		long id,
		String name,
		Integer displayOrder,
		long ownerId,
		boolean isDeleted,
		LocalDateTime deletedTimestamp

) {
}
