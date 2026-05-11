package com.productservice.services.impls;

import com.productservice.models.Category;
import com.productservice.repositories.CategoryRepository;
import com.productservice.services.CategoryGetService;
import com.utility.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryGetServiceImpl implements CategoryGetService {

	private final CategoryRepository categoryRepository;

	@Override
	public Category getById(long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
	}


}