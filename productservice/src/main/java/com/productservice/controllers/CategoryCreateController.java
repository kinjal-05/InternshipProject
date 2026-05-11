package com.productservice.controllers;

import com.productservice.dtos.CategoryRequest;
import com.productservice.dtos.CategoryResponse;
import com.productservice.services.CategoryCreateService;
import com.utility.config.MessageService;
import com.utility.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCreateController {

	private final CategoryCreateService categoryCreateService;
	private final MessageService messageService;

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
			@RequestBody @Valid CategoryRequest request,@RequestHeader("X-User-Id") String userIdHeader) {

		long ownerId = Long.parseLong(userIdHeader);
		CategoryResponse response = categoryCreateService.create(request,ownerId);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(response, messageService.getMessage(
						"category.create.success"
				)));
	}
}
