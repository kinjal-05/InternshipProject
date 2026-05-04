package einfochips.controllers;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.einfochips.controllers.ProductUpdateController;
import com.einfochips.dtos.ApiResponse;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.dtos.ProductUpdateRequestDTO;
import com.einfochips.services.ProductUpdateService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductUpdateControllerTest {

	@Mock
	private ProductUpdateService productUpdateService;

	@InjectMocks
	private ProductUpdateController productUpdateController;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should update product successfully")
	void testUpdateProductSuccess() {

		long id = 1L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Laptop Updated",
						"Gaming Laptop Updated",
						"Electronics",
						new BigDecimal("60000"),
						15
				);

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Laptop Updated",
						"Gaming Laptop Updated",
						"Electronics",
						new BigDecimal("60000"),
						15,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productUpdateService.updateProduct(id, request))
				.thenReturn(response);

		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productUpdateController.updateProduct(id, request);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(
				"Product updated successfully",
				result.getBody().getMessage()
		);

		assertEquals(id,
				result.getBody().getData().id());

		assertEquals("Laptop Updated",
				result.getBody().getData().name());

		verify(productUpdateService, times(1))
				.updateProduct(id, request);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(
			name = "[{index}] id={0}, name={1}, category={2}"
	)
	@MethodSource("updateProvider")
	@DisplayName("Should update various products successfully")
	void testUpdateProductParameterized(
			long id,
			String name,
			String category,
			BigDecimal price,
			Integer stock) {

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						name,
						"Updated Description",
						category,
						price,
						stock
				);

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						name,
						"Updated Description",
						category,
						price,
						stock,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productUpdateService.updateProduct(id, request))
				.thenReturn(response);

		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productUpdateController.updateProduct(id, request);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertEquals(
				"Product updated successfully",
				result.getBody().getMessage()
		);

		assertEquals(name,
				result.getBody().getData().name());

		assertEquals(category,
				result.getBody().getData().category());

		assertEquals(price,
				result.getBody().getData().price());

		assertEquals(stock,
				result.getBody().getData().stockQuantity());

		verify(productUpdateService, times(1))
				.updateProduct(id, request);
	}

	static Stream<Arguments> updateProvider() {
		return Stream.of(
				Arguments.of(
						1L,
						"Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10
				),
				Arguments.of(
						2L,
						"Phone",
						"Mobiles",
						new BigDecimal("25000"),
						20
				),
				Arguments.of(
						3L,
						"Keyboard",
						"Accessories",
						new BigDecimal("1500"),
						30
				),
				Arguments.of(
						4L,
						"Shoes",
						"Fashion",
						new BigDecimal("2999"),
						40
				),
				Arguments.of(
						5L,
						"Book",
						"Education",
						new BigDecimal("499"),
						50
				)
		);
	}

	// ---------------------------------------------------
	// PARTIAL UPDATE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should partially update product")
	void testPartialUpdateProduct() {

		long id = 2L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						null,
						null,
						"Electronics",
						null,
						99
				);

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Existing Name",
						"Existing Desc",
						"Electronics",
						new BigDecimal("1000"),
						99,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productUpdateService.updateProduct(id, request))
				.thenReturn(response);

		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productUpdateController.updateProduct(id, request);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertEquals("Electronics",
				result.getBody().getData().category());

		assertEquals(99,
				result.getBody().getData().stockQuantity());

		verify(productUpdateService)
				.updateProduct(id, request);
	}

	// ---------------------------------------------------
	// EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when product not found")
	void testUpdateProductThrowsException() {

		long id = 999L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Test",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						5
				);

		when(productUpdateService.updateProduct(id, request))
				.thenThrow(
						new RuntimeException(
								"Product not found"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productUpdateController
								.updateProduct(id, request)
				);

		assertEquals(
				"Product not found",
				ex.getMessage()
		);

		verify(productUpdateService)
				.updateProduct(id, request);
	}

	// ---------------------------------------------------
	// NULL RESPONSE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should handle null response")
	void testUpdateProductNullResponse() {

		long id = 10L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Name",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						2
				);

		when(productUpdateService.updateProduct(id, request))
				.thenReturn(null);

		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productUpdateController.updateProduct(id, request);

		assertEquals(HttpStatus.OK,
				result.getStatusCode());

		assertNull(result.getBody().getData());

		assertEquals(
				"Product updated successfully",
				result.getBody().getMessage()
		);

		verify(productUpdateService)
				.updateProduct(id, request);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Service should be called exactly once")
	void testServiceCalledOnlyOnce() {

		long id = 7L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						10
				);

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						10,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productUpdateService.updateProduct(id, request))
				.thenReturn(response);

		productUpdateController.updateProduct(id, request);

		verify(productUpdateService, times(1))
				.updateProduct(id, request);

		verifyNoMoreInteractions(
				productUpdateService
		);
	}
}
