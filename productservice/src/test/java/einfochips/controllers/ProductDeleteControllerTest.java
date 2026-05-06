package einfochips.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import com.einfochips.productservice.controllers.ProductDeleteController;
import com.einfochips.utility.dtos.ApiResponse;
import com.einfochips.productservice.services.ProductDeleteService;

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
class ProductDeleteControllerTest {

	@Mock
	private ProductDeleteService productDeleteService;

	@InjectMocks
	private ProductDeleteController productDeleteController;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should delete product successfully")
	void testDeleteProductSuccess() {

		// Arrange
		long id = 1L;
		doNothing().when(productDeleteService).deleteProduct(id);

		// Act
		ResponseEntity<ApiResponse<Void>> result =
				productDeleteController.deleteProduct(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("Product deleted successfully", result.getBody().message());
		assertNull(result.getBody().data());

		verify(productDeleteService, times(1)).deleteProduct(id);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] Delete product id = {0}")
	@MethodSource("idProvider")
	@DisplayName("Should delete multiple products successfully")
	void testDeleteProductParameterized(long id) {

		// Arrange
		doNothing().when(productDeleteService).deleteProduct(id);

		// Act
		ResponseEntity<ApiResponse<Void>> result =
				productDeleteController.deleteProduct(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals("Product deleted successfully", result.getBody().message());
		assertNull(result.getBody().data());

		verify(productDeleteService, times(1)).deleteProduct(id);
	}

	static Stream<Arguments> idProvider() {
		return Stream.of(
				Arguments.of(1L),
				Arguments.of(10L),
				Arguments.of(50L),
				Arguments.of(99L),
				Arguments.of(100L)
		);
	}

	// ---------------------------------------------------
	// EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when product not found")
	void testDeleteProductThrowsException() {

		// Arrange
		long id = 999L;

		doThrow(new RuntimeException("Product not found"))
				.when(productDeleteService)
				.deleteProduct(id);

		// Act + Assert
		RuntimeException ex = assertThrows(RuntimeException.class,
				() -> productDeleteController.deleteProduct(id));

		assertEquals("Product not found", ex.getMessage());

		verify(productDeleteService, times(1)).deleteProduct(id);
	}

	// ---------------------------------------------------
	// VERIFY EXACTLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Service should be called exactly once")
	void testDeleteCalledOnlyOnce() {

		// Arrange
		long id = 5L;
		doNothing().when(productDeleteService).deleteProduct(id);

		// Act
		productDeleteController.deleteProduct(id);

		// Assert
		verify(productDeleteService, times(1)).deleteProduct(id);
		verifyNoMoreInteractions(productDeleteService);
	}

	// ---------------------------------------------------
	// ZERO ID TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should allow delete when id is zero")
	void testDeleteProductZeroId() {

		// Arrange
		long id = 0L;
		doNothing().when(productDeleteService).deleteProduct(id);

		// Act
		ResponseEntity<ApiResponse<Void>> result =
				productDeleteController.deleteProduct(id);

		// Assert
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals("Product deleted successfully", result.getBody().message());

		verify(productDeleteService).deleteProduct(id);
	}
}