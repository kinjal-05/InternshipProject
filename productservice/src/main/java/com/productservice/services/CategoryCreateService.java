package com.productservice.services;

import com.productservice.dtos.CategoryRequest;
import com.productservice.dtos.CategoryResponse;

public interface CategoryCreateService
{
	CategoryResponse create(CategoryRequest category,long own
	);
}