package productservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.productservice.dtos.ProductMapper;
import com.productservice.dtos.ProductRequestDTO;
import com.productservice.dtos.ProductResponseDTO;
import com.productservice.models.Product;
import com.productservice.repositories.ProductRepository;

import com.productservice.services.impls.ProductCreateServiceImpl;
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
class ProductCreateServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductCreateServiceImpl productCreateService;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should create product successfully")
	void testCreateProductSuccess() {

		ProductRequestDTO request =
				new ProductRequestDTO(
						"Laptop",
						"Gaming Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10
				);

		Product entity = Product.builder()
				.name("Laptop")
				.description("Gaming Laptop")
				.category("Electronics")
				.price(new BigDecimal("55000"))
				.stockQuantity(10)
				.build();

		Product saved = Product.builder()
				.id(1L)
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
						1L,
						"Laptop",
						"Gaming Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10,
						saved.getCreatedAt(),
						saved.getUpdatedAt()
				);

		when(productMapper.toEntity(request))
				.thenReturn(entity);

		when(productRepository.save(entity))
				.thenReturn(saved);

		when(productMapper.toResponseDTO(saved))
				.thenReturn(response);

		ProductResponseDTO result =
				productCreateService.createProduct(request);

		assertNotNull(result);
		assertEquals(1L, result.id());
		assertEquals("Laptop", result.name());

		verify(productMapper).toEntity(request);
		verify(productRepository).save(entity);
		verify(productMapper).toResponseDTO(saved);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(
			name = "[{index}] name={0}, category={1}"
	)
	@MethodSource("productProvider")
	@DisplayName("Should create various products")
	void testCreateProductParameterized(
			String name,
			String category,
			BigDecimal price,
			int stock) {

		ProductRequestDTO request =
				new ProductRequestDTO(
						name,
						"Description",
						category,
						price,
						stock
				);

		Product entity = Product.builder()
				.name(name)
				.description("Description")
				.category(category)
				.price(price)
				.stockQuantity(stock)
				.build();

		Product saved = Product.builder()
				.id(1L)
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
						1L,
						name,
						"Description",
						category,
						price,
						stock,
						saved.getCreatedAt(),
						saved.getUpdatedAt()
				);

		when(productMapper.toEntity(request))
				.thenReturn(entity);

		when(productRepository.save(entity))
				.thenReturn(saved);

		when(productMapper.toResponseDTO(saved))
				.thenReturn(response);

		ProductResponseDTO result =
				productCreateService.createProduct(request);

		assertEquals(name, result.name());
		assertEquals(category, result.category());
		assertEquals(price, result.price());
		assertEquals(stock, result.stockQuantity());

		verify(productMapper).toEntity(request);
		verify(productRepository).save(entity);
		verify(productMapper).toResponseDTO(saved);
	}

	static Stream<Arguments> productProvider() {
		return Stream.of(
				Arguments.of(
						"Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10
				),
				Arguments.of(
						"Phone",
						"Mobiles",
						new BigDecimal("25000"),
						20
				),
				Arguments.of(
						"Keyboard",
						"Accessories",
						new BigDecimal("1500"),
						30
				),
				Arguments.of(
						"Shoes",
						"Fashion",
						new BigDecimal("2999"),
						40
				),
				Arguments.of(
						"Book",
						"Education",
						new BigDecimal("499"),
						50
				)
		);
	}

	// ---------------------------------------------------
	// NULL SAVE TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should handle null saved entity")
	void testCreateProductNullSavedEntity() {

		ProductRequestDTO request =
				new ProductRequestDTO(
						"Test",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						5
				);

		Product entity = Product.builder().build();

		when(productMapper.toEntity(request))
				.thenReturn(entity);

		when(productRepository.save(entity))
				.thenReturn(null);

		when(productMapper.toResponseDTO(null))
				.thenReturn(null);

		ProductResponseDTO result =
				productCreateService.createProduct(request);

		assertNull(result);

		verify(productMapper).toEntity(request);
		verify(productRepository).save(entity);
		verify(productMapper).toResponseDTO(null);
	}

	// ---------------------------------------------------
	// EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when save fails")
	void testCreateProductThrowsException() {

		ProductRequestDTO request =
				new ProductRequestDTO(
						"Test",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						5
				);

		Product entity = Product.builder().build();

		when(productMapper.toEntity(request))
				.thenReturn(entity);

		when(productRepository.save(entity))
				.thenThrow(
						new RuntimeException(
								"Database error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productCreateService
								.createProduct(request)
				);

		assertEquals(
				"Database error",
				ex.getMessage()
		);

		verify(productMapper).toEntity(request);
		verify(productRepository).save(entity);
		verify(productMapper, never())
				.toResponseDTO(any());
	}

	// ---------------------------------------------------
	// VERIFY EXACTLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Dependencies should be called once")
	void testDependencyCalledOnce() {

		ProductRequestDTO request =
				new ProductRequestDTO(
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						5
				);

		Product entity = Product.builder().build();
		Product saved = Product.builder().build();

		ProductResponseDTO response =
				new ProductResponseDTO(
						1L,
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						5,
						LocalDateTime.now(),
						LocalDateTime.now()
				);

		when(productMapper.toEntity(request))
				.thenReturn(entity);

		when(productRepository.save(entity))
				.thenReturn(saved);

		when(productMapper.toResponseDTO(saved))
				.thenReturn(response);

		productCreateService.createProduct(request);

		verify(productMapper, times(1))
				.toEntity(request);

		verify(productRepository, times(1))
				.save(entity);

		verify(productMapper, times(1))
				.toResponseDTO(saved);

		verifyNoMoreInteractions(
				productRepository,
				productMapper
		);
	}
}