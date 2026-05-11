package com.productservice.controllers;

import com.productservice.repositories.CategoryRepository;
import com.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ProductService — InternalOwnershipController.java
@RestController
@RequestMapping("/internal")
public class InternalOwnershipController {

	@Autowired private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	// Called by Gateway to check product → category → owner
	@GetMapping("/products/{productId}/owner-check")
	public ResponseEntity<Boolean> isProductOwner(
			@PathVariable long productId,
			@RequestParam long userId) {
		return productRepository.findById(productId)
				.map(product -> ResponseEntity.ok(product.getCategory().getOwnerId() == userId))
				.orElse(ResponseEntity.ok(false));
	}

	// Called by Gateway to check category owner
	@GetMapping("/categories/{categoryId}/owner-check")
	public ResponseEntity<Boolean> isCategoryOwner(
			@PathVariable long categoryId,
			@RequestParam long userId) {
		return categoryRepository.findById(categoryId)
				.map(cat -> ResponseEntity.ok(cat.getOwnerId() == userId))
				.orElse(ResponseEntity.ok(false));
	}
}
