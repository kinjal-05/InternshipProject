package com.productservice.controllers;

import com.productservice.models.Category;
import com.utility.config.MessageService;
import com.utility.dtos.ApiResponse;
import com.productservice.dtos.CategoryResponse;
import com.productservice.services.CategoryGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryGetController {

	private final CategoryGetService categoryGetService;
	private final MessageService messageService;



	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Category>> getCategoryById(
			@PathVariable long id) {


		Category response = categoryGetService.getById(id);
		return ResponseEntity.ok(
				ApiResponse.success(response, messageService.getMessage(
						"category.get.success"
				))
		);
	}
}