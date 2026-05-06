package einfochips.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.einfochips.productservice.controllers.ProductSearchController;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.productservice.dtos.ProductResponseDTO;
import com.einfochips.productservice.services.ProductSearchService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductSearchControllerTest {

	@Mock
	private ProductSearchService productSearchService;

	@InjectMocks
	private ProductSearchController productSearchController;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should fetch products successfully")
	void testSearchProductsSuccess() {

		Pageable pageable = PageRequest.of(
				0,
				10,
				Sort.by(Sort.Direction.DESC, "createdAt")
		);

		ProductResponseDTO dto = new ProductResponseDTO(
				1L,
				"Laptop",
				"Gaming Laptop",
				"Electronics",
				new BigDecimal("55000"),
				10,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		Page<ProductResponseDTO> page =
				new PageImpl<>(List.of(dto), pageable, 1);

		when(productSearchService.searchProducts(
				null, null, pageable))
				.thenReturn(page);

		ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> result =
				productSearchController.searchProducts(
						null, null, pageable);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(
				"Products fetched successfully",
				result.getBody().message()
		);
		assertEquals(1,
				result.getBody().data().getTotalElements());

		verify(productSearchService, times(1))
				.searchProducts(null, null, pageable);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(
			name = "[{index}] search={0}, category={1}"
	)
	@MethodSource("searchProvider")
	@DisplayName("Should search products with filters")
	void testSearchProductsParameterized(
			String search,
			String category) {

		Pageable pageable = PageRequest.of(0, 5);

		ProductResponseDTO dto = new ProductResponseDTO(
				1L,
				"Product",
				"Description",
				category,
				new BigDecimal("999"),
				5,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		Page<ProductResponseDTO> page =
				new PageImpl<>(List.of(dto), pageable, 1);

		when(productSearchService.searchProducts(
				search, category, pageable))
				.thenReturn(page);

		ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> result =
				productSearchController.searchProducts(
						search, category, pageable);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertEquals(
				"Products fetched successfully",
				result.getBody().message()
		);

		assertEquals(1,
				result.getBody().data()
						.getContent().size());

		verify(productSearchService, times(1))
				.searchProducts(search, category, pageable);
	}

	static Stream<Arguments> searchProvider() {
		return Stream.of(
				Arguments.of(null, null),
				Arguments.of("bag", null),
				Arguments.of(null, "electronics"),
				Arguments.of("shoe", "fashion"),
				Arguments.of("book", "education")
		);
	}

	// ---------------------------------------------------
	// EMPTY PAGE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should return empty page when no products found")
	void testSearchProductsEmptyPage() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductResponseDTO> emptyPage =
				Page.empty(pageable);

		when(productSearchService.searchProducts(
				null, null, pageable))
				.thenReturn(emptyPage);

		ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> result =
				productSearchController.searchProducts(
						null, null, pageable);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertTrue(result.getBody()
				.data()
				.getContent()
				.isEmpty());

		assertEquals(0,
				result.getBody()
						.data()
						.getTotalElements());

		verify(productSearchService)
				.searchProducts(null, null, pageable);
	}

	// ---------------------------------------------------
	// EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when service fails")
	void testSearchProductsThrowsException() {

		Pageable pageable = PageRequest.of(0, 10);

		when(productSearchService.searchProducts(
				"bag", "kids", pageable))
				.thenThrow(
						new RuntimeException("DB Error")
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productSearchController
								.searchProducts(
										"bag",
										"kids",
										pageable
								)
				);

		assertEquals("DB Error",
				ex.getMessage());

		verify(productSearchService)
				.searchProducts(
						"bag",
						"kids",
						pageable
				);
	}

	// ---------------------------------------------------
	// NULL PAGE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should handle null page response")
	void testSearchProductsNullResponse() {

		Pageable pageable = PageRequest.of(0, 10);

		when(productSearchService.searchProducts(
				null, null, pageable))
				.thenReturn(null);

		ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> result =
				productSearchController.searchProducts(
						null, null, pageable);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertNull(result.getBody().data());

		assertEquals(
				"Products fetched successfully",
				result.getBody().message()
		);

		verify(productSearchService)
				.searchProducts(null, null, pageable);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Service should be called exactly once")
	void testServiceCalledOnlyOnce() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductResponseDTO> page =
				Page.empty(pageable);

		when(productSearchService.searchProducts(
				null, null, pageable))
				.thenReturn(page);

		productSearchController.searchProducts(
				null, null, pageable);

		verify(productSearchService, times(1))
				.searchProducts(null, null, pageable);

		verifyNoMoreInteractions(
				productSearchService);
	}
}