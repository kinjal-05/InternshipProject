package einfochips.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.einfochips.controllers.ProductGetByIdController;
import com.einfochips.dtos.ApiResponse;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.services.ProductGetByIdService;

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
class ProductGetByIdControllerTest {

	@Mock
	private ProductGetByIdService productGetByIdService;

	@InjectMocks
	private ProductGetByIdController productGetByIdController;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should fetch product by id successfully")
	void testGetProductByIdSuccess() {

		// Arrange
		long id = 1L;

		ProductResponseDTO response = new ProductResponseDTO(
				id,
				"Laptop",
				"Gaming Laptop",
				"Electronics",
				new BigDecimal("55000"),
				10,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		when(productGetByIdService.getProductById(id)).thenReturn(response);

		// Act
		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productGetByIdController.getProductById(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("Product fetched successfully", result.getBody().getMessage());

		assertNotNull(result.getBody().getData());
		assertEquals(id, result.getBody().getData().id());
		assertEquals("Laptop", result.getBody().getData().name());

		verify(productGetByIdService, times(1)).getProductById(id);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] id={0}, name={1}, category={2}")
	@MethodSource("productProvider")
	@DisplayName("Should fetch various products successfully")
	void testGetProductByIdParameterized(
			long id,
			String name,
			String category,
			BigDecimal price,
			int stockQuantity) {

		// Arrange
		ProductResponseDTO response = new ProductResponseDTO(
				id,
				name,
				"Some description",
				category,
				price,
				stockQuantity,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		when(productGetByIdService.getProductById(id)).thenReturn(response);

		// Act
		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productGetByIdController.getProductById(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("Product fetched successfully", result.getBody().getMessage());

		assertEquals(id, result.getBody().getData().id());
		assertEquals(name, result.getBody().getData().name());
		assertEquals(category, result.getBody().getData().category());
		assertEquals(price, result.getBody().getData().price());
		assertEquals(stockQuantity, result.getBody().getData().stockQuantity());

		verify(productGetByIdService, times(1)).getProductById(id);
	}

	static Stream<Arguments> productProvider() {
		return Stream.of(
				Arguments.of(1L, "Laptop", "Electronics", new BigDecimal("55000"), 10),
				Arguments.of(2L, "Phone", "Mobiles", new BigDecimal("25000"), 15),
				Arguments.of(3L, "Keyboard", "Accessories", new BigDecimal("1500"), 30),
				Arguments.of(4L, "Shoes", "Fashion", new BigDecimal("2999"), 20),
				Arguments.of(5L, "Book", "Education", new BigDecimal("499"), 50)
		);
	}

	// ---------------------------------------------------
	// EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when product not found")
	void testGetProductByIdThrowsException() {

		// Arrange
		long id = 999L;

		when(productGetByIdService.getProductById(id))
				.thenThrow(new RuntimeException("Product not found"));

		// Act + Assert
		RuntimeException ex = assertThrows(RuntimeException.class,
				() -> productGetByIdController.getProductById(id));

		assertEquals("Product not found", ex.getMessage());

		verify(productGetByIdService, times(1)).getProductById(id);
	}

	// ---------------------------------------------------
	// NULL RESPONSE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should handle null response")
	void testGetProductByIdNullResponse() {

		// Arrange
		long id = 10L;

		when(productGetByIdService.getProductById(id)).thenReturn(null);

		// Act
		ResponseEntity<ApiResponse<ProductResponseDTO>> result =
				productGetByIdController.getProductById(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertNull(result.getBody().getData());
		assertEquals("Product fetched successfully", result.getBody().getMessage());

		verify(productGetByIdService).getProductById(id);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Service should be called exactly once")
	void testServiceCalledOnlyOnce() {

		// Arrange
		long id = 7L;

		ProductResponseDTO response = new ProductResponseDTO(
				id,
				"Watch",
				"Smart Watch",
				"Accessories",
				new BigDecimal("5999"),
				12,
				LocalDateTime.now(),
				LocalDateTime.now()
		);

		when(productGetByIdService.getProductById(id)).thenReturn(response);

		// Act
		productGetByIdController.getProductById(id);

		// Assert
		verify(productGetByIdService, times(1)).getProductById(id);
		verifyNoMoreInteractions(productGetByIdService);
	}
}