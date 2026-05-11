package com.productservice.services.impls;

import com.productservice.dtos.CategoryRequest;
import com.productservice.dtos.CategoryResponse;
import com.productservice.models.Category;
import com.productservice.repositories.CategoryRepository;
import com.productservice.services.CategoryCreateService;
import com.utility.exceptions.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCreateServiceImpl implements CategoryCreateService {

	private final CategoryRepository categoryRepository;


	@Override
	@Transactional
	public CategoryResponse create(CategoryRequest request,long ownerId
	) {

		if (categoryRepository.existsByNameAndIsDeletedFalse(request.name())) {
			throw new ResourceAlreadyExistsException(
					"Category with name '" + request.name() + "' already exists"
			);
		}

		Category category = Category.builder()
				.name(request.name())
				.displayOrder(request.displayOrder())
				.ownerId(ownerId)
				.build();


			return toResponse(categoryRepository.save(category));

	}

	private CategoryResponse toResponse(Category saved) {
		return new CategoryResponse(
				saved.getId(),
				saved.getName(),
				saved.getDisplayOrder(),
				saved.getOwnerId(),
				saved.isDeleted(),
				saved.getDeletedTimestamp()
		);
	}



}