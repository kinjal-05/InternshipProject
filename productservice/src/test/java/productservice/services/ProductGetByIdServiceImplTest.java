package productservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import com.productservice.dtos.ProductMapper;
import com.productservice.dtos.ProductResponseDTO;
import com.utility.exceptions.ResourceNotFoundException;
import com.productservice.models.Product;
import com.productservice.repositories.ProductRepository;

import com.productservice.services.impls.ProductGetByIdServiceImpl;
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
class ProductGetByIdServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductGetByIdServiceImpl productGetByIdService;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should fetch product successfully")
	void testGetProductByIdSuccess() {

		long id = 1L;

		Product product = Product.builder()
				.id(id)
				.name("Laptop")
				.description("Gaming Laptop")
				.category("Electronics")
				.price(new BigDecimal("55000"))
				.stockQuantity(10)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Laptop",
						"Gaming Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10,
						product.getCreatedAt(),
						product.getUpdatedAt()
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(product));

		when(productMapper.toResponseDTO(product))
				.thenReturn(response);

		ProductResponseDTO result =
				productGetByIdService.getProductById(id);

		assertNotNull(result);
		assertEquals(id, result.id());
		assertEquals("Laptop", result.name());

		verify(productRepository).findActiveById(id);
		verify(productMapper).toResponseDTO(product);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] id={0}, name={1}")
	@MethodSource("productProvider")
	@DisplayName("Should fetch various products")
	void testGetProductByIdParameterized(
			long id,
			String name,
			String category,
			BigDecimal price,
			int stock) {

		Product product = Product.builder()
				.id(id)
				.name(name)
				.description("Description")
				.category(category)
				.price(price)
				.stockQuantity(stock)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						name,
						"Description",
						category,
						price,
						stock,
						product.getCreatedAt(),
						product.getUpdatedAt()
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(product));

		when(productMapper.toResponseDTO(product))
				.thenReturn(response);

		ProductResponseDTO result =
				productGetByIdService.getProductById(id);

		assertEquals(id, result.id());
		assertEquals(name, result.name());
		assertEquals(category, result.category());
		assertEquals(price, result.price());

		verify(productRepository).findActiveById(id);
		verify(productMapper).toResponseDTO(product);
	}

	static Stream<Arguments> productProvider() {
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
	// NOT FOUND TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw ResourceNotFoundException")
	void testGetProductByIdNotFound() {

		long id = 999L;

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.empty());

		ResourceNotFoundException ex =
				assertThrows(
						ResourceNotFoundException.class,
						() -> productGetByIdService.getProductById(id)
				);

		assertEquals(
				"Product not found with ID: 999",
				ex.getMessage()
		);

		verify(productRepository).findActiveById(id);
		verify(productMapper, never())
				.toResponseDTO(any());
	}

	// ---------------------------------------------------
	// REPOSITORY EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw repository exception")
	void testRepositoryException() {

		long id = 5L;

		when(productRepository.findActiveById(id))
				.thenThrow(
						new RuntimeException(
								"Database error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productGetByIdService.getProductById(id)
				);

		assertEquals(
				"Database error",
				ex.getMessage()
		);

		verify(productRepository).findActiveById(id);
		verify(productMapper, never())
				.toResponseDTO(any());
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Dependencies should be called once")
	void testDependenciesCalledOnce() {

		long id = 7L;

		Product product = Product.builder()
				.id(id)
				.build();

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						5,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(product));

		when(productMapper.toResponseDTO(product))
				.thenReturn(response);

		productGetByIdService.getProductById(id);

		verify(productRepository, times(1))
				.findActiveById(id);

		verify(productMapper, times(1))
				.toResponseDTO(product);

		verifyNoMoreInteractions(
				productRepository,
				productMapper
		);
	}
}
