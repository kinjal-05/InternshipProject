package einfochips.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import com.einfochips.exceptions.ProductNotFoundException;
import com.einfochips.repositories.ProductRepository;

import com.einfochips.services.impls.ProductDeleteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDeleteServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductDeleteServiceImpl productDeleteService;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should delete product successfully")
	void testDeleteProductSuccess() {

		long id = 1L;

		when(productRepository.delete(id))
				.thenReturn(1);

		assertDoesNotThrow(() ->
				productDeleteService.deleteProduct(id));

		verify(productRepository, times(1))
				.delete(id);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] id={0}")
	@MethodSource("successIds")
	@DisplayName("Should delete multiple products successfully")
	void testDeleteProductParameterized(long id) {

		when(productRepository.delete(id))
				.thenReturn(1);

		assertDoesNotThrow(() ->
				productDeleteService.deleteProduct(id));

		verify(productRepository, times(1))
				.delete(id);
	}

	static Stream<Arguments> successIds() {
		return Stream.of(
				Arguments.of(1L),
				Arguments.of(10L),
				Arguments.of(25L),
				Arguments.of(50L),
				Arguments.of(100L)
		);
	}

	// ---------------------------------------------------
	// NOT FOUND TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw ProductNotFoundException when no rows affected")
	void testDeleteProductNotFound() {

		long id = 999L;

		when(productRepository.delete(id))
				.thenReturn(0);

		ProductNotFoundException ex =
				assertThrows(
						ProductNotFoundException.class,
						() -> productDeleteService.deleteProduct(id)
				);

		assertEquals(
				"Product not found with ID: 999",
				ex.getMessage()
		);

		verify(productRepository, times(1))
				.delete(id);
	}

	// ---------------------------------------------------
	// PARAMETERIZED NOT FOUND TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] invalid id={0}")
	@MethodSource("invalidIds")
	@DisplayName("Should throw exception for invalid ids")
	void testDeleteProductNotFoundParameterized(long id) {

		when(productRepository.delete(id))
				.thenReturn(0);

		ProductNotFoundException ex =
				assertThrows(
						ProductNotFoundException.class,
						() -> productDeleteService.deleteProduct(id)
				);

		assertEquals(
				"Product not found with ID: " + id,
				ex.getMessage()
		);

		verify(productRepository)
				.delete(id);
	}

	static Stream<Arguments> invalidIds() {
		return Stream.of(
				Arguments.of(0L),
				Arguments.of(-1L),
				Arguments.of(500L),
				Arguments.of(999L),
				Arguments.of(1000L)
		);
	}

	// ---------------------------------------------------
	// REPOSITORY EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when repository fails")
	void testDeleteProductRepositoryException() {

		long id = 5L;

		when(productRepository.delete(id))
				.thenThrow(
						new RuntimeException(
								"Database error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productDeleteService.deleteProduct(id)
				);

		assertEquals(
				"Database error",
				ex.getMessage()
		);

		verify(productRepository, times(1))
				.delete(id);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Repository should be called exactly once")
	void testRepositoryCalledOnlyOnce() {

		long id = 7L;

		when(productRepository.delete(id))
				.thenReturn(1);

		productDeleteService.deleteProduct(id);

		verify(productRepository, times(1))
				.delete(id);

		verifyNoMoreInteractions(
				productRepository
		);
	}
}