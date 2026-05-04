package einfochips.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.einfochips.dtos.ProductMapper;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.models.Product;
import com.einfochips.repositories.ProductRepository;

import com.einfochips.services.impls.ProductSearchServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductSearchServiceImpl productSearchService;

	// ---------------------------------------------------
	// SUCCESS TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should search products successfully")
	void testSearchProductsSuccess() {

		Pageable pageable = PageRequest.of(0, 10);

		Product product = Product.builder()
				.id(1L)
				.name("Laptop")
				.description("Gaming Laptop")
				.category("Electronics")
				.price(new BigDecimal("55000"))
				.stockQuantity(10)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		ProductResponseDTO dto =
				new ProductResponseDTO(
						1L,
						"Laptop",
						"Gaming Laptop",
						"Electronics",
						new BigDecimal("55000"),
						10,
						product.getCreatedAt(),
						product.getUpdatedAt()
				);

		Page<Product> productPage =
				new PageImpl<>(List.of(product), pageable, 1);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenReturn(productPage);

		when(productMapper.toResponseDTO(product))
				.thenReturn(dto);

		Page<ProductResponseDTO> result =
				productSearchService.searchProducts(
						null,
						null,
						pageable
				);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Laptop",
				result.getContent().get(0).name());

		verify(productRepository)
				.findAll(any(Specification.class), eq(pageable));

		verify(productMapper)
				.toResponseDTO(product);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(
			name = "[{index}] search={0}, category={1}"
	)
	@MethodSource("searchProvider")
	@DisplayName("Should search various filters")
	void testSearchProductsParameterized(
			String search,
			String category) {

		Pageable pageable = PageRequest.of(0, 5);

		Product product = Product.builder()
				.id(1L)
				.name("Product")
				.description("Description")
				.category(category)
				.price(new BigDecimal("999"))
				.stockQuantity(5)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		ProductResponseDTO dto =
				new ProductResponseDTO(
						1L,
						"Product",
						"Description",
						category,
						new BigDecimal("999"),
						5,
						product.getCreatedAt(),
						product.getUpdatedAt()
				);

		Page<Product> page =
				new PageImpl<>(List.of(product), pageable, 1);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenReturn(page);

		when(productMapper.toResponseDTO(product))
				.thenReturn(dto);

		Page<ProductResponseDTO> result =
				productSearchService.searchProducts(
						search,
						category,
						pageable
				);

		assertEquals(1, result.getTotalElements());
		assertEquals(category,
				result.getContent().get(0).category());

		verify(productRepository)
				.findAll(any(Specification.class), eq(pageable));

		verify(productMapper)
				.toResponseDTO(product);
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
	@DisplayName("Should return empty page")
	void testSearchProductsEmptyPage() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<Product> emptyPage =
				Page.empty(pageable);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenReturn(emptyPage);

		Page<ProductResponseDTO> result =
				productSearchService.searchProducts(
						null,
						null,
						pageable
				);

		assertNotNull(result);
		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());

		verify(productRepository)
				.findAll(any(Specification.class), eq(pageable));

		verify(productMapper, never())
				.toResponseDTO(any());
	}

	// ---------------------------------------------------
	// REPOSITORY EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when repository fails")
	void testSearchProductsRepositoryException() {

		Pageable pageable = PageRequest.of(0, 10);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenThrow(
						new RuntimeException(
								"Database error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productSearchService
								.searchProducts(
										"bag",
										"kids",
										pageable
								)
				);

		assertEquals(
				"Database error",
				ex.getMessage()
		);

		verify(productRepository)
				.findAll(any(Specification.class), eq(pageable));
	}

	// ---------------------------------------------------
	// MAPPER EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when mapper fails")
	void testSearchProductsMapperException() {

		Pageable pageable = PageRequest.of(0, 10);

		Product product = Product.builder().build();

		Page<Product> page =
				new PageImpl<>(List.of(product), pageable, 1);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenReturn(page);

		when(productMapper.toResponseDTO(product))
				.thenThrow(
						new RuntimeException(
								"Mapping error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productSearchService
								.searchProducts(
										null,
										null,
										pageable
								)
				);

		assertEquals(
				"Mapping error",
				ex.getMessage()
		);

		verify(productMapper)
				.toResponseDTO(product);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Dependencies should be called once")
	void testDependenciesCalledOnce() {

		Pageable pageable = PageRequest.of(0, 10);

		Product product = Product.builder().build();

		ProductResponseDTO dto =
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

		Page<Product> page =
				new PageImpl<>(List.of(product), pageable, 1);

		when(productRepository.findAll(
				any(Specification.class),
				eq(pageable)))
				.thenReturn(page);

		when(productMapper.toResponseDTO(product))
				.thenReturn(dto);

		productSearchService.searchProducts(
				null,
				null,
				pageable
		);

		verify(productRepository, times(1))
				.findAll(any(Specification.class), eq(pageable));

		verify(productMapper, times(1))
				.toResponseDTO(product);

		verifyNoMoreInteractions(
				productRepository,
				productMapper
		);
	}
}