package einfochips.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import com.einfochips.dtos.ProductMapper;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.dtos.ProductUpdateRequestDTO;
import com.einfochips.exceptions.ResourceNotFoundException;
import com.einfochips.models.Product;
import com.einfochips.repositories.ProductRepository;

import com.einfochips.services.impls.ProductUpdateServiceImpl;
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
class ProductUpdateServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductUpdateServiceImpl productUpdateService;

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
						"Gaming Updated",
						"Electronics",
						new BigDecimal("60000"),
						15
				);

		Product existing = Product.builder()
				.id(id)
				.name("Laptop")
				.description("Gaming")
				.category("Electronics")
				.price(new BigDecimal("55000"))
				.stockQuantity(10)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		Product updated = Product.builder()
				.id(id)
				.name("Laptop Updated")
				.description("Gaming Updated")
				.category("Electronics")
				.price(new BigDecimal("60000"))
				.stockQuantity(15)
				.createdAt(existing.getCreatedAt())
				.updatedAt(LocalDateTime.now())
				.build();

		ProductResponseDTO response =
				new ProductResponseDTO(
						id,
						"Laptop Updated",
						"Gaming Updated",
						"Electronics",
						new BigDecimal("60000"),
						15,
						updated.getCreatedAt(),
						updated.getUpdatedAt()
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(existing));

		doAnswer(invocation -> {
			Product target = invocation.getArgument(1);
			target.setName("Laptop Updated");
			target.setDescription("Gaming Updated");
			target.setPrice(new BigDecimal("60000"));
			target.setStockQuantity(15);
			return null;
		}).when(productMapper).updateEntity(request, existing);

		when(productRepository.save(existing))
				.thenReturn(updated);

		when(productMapper.toResponseDTO(updated))
				.thenReturn(response);

		ProductResponseDTO result =
				productUpdateService.updateProduct(id, request);

		assertNotNull(result);
		assertEquals(id, result.id());
		assertEquals("Laptop Updated", result.name());

		verify(productRepository).findActiveById(id);
		verify(productMapper).updateEntity(request, existing);
		verify(productRepository).save(existing);
		verify(productMapper).toResponseDTO(updated);
	}

	// ---------------------------------------------------
	// PARAMETERIZED TEST
	// ---------------------------------------------------
	@ParameterizedTest(name = "[{index}] id={0}, name={1}")
	@MethodSource("updateProvider")
	@DisplayName("Should update various products")
	void testUpdateProductParameterized(
			long id,
			String name,
			String category,
			BigDecimal price,
			int stock) {

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						name,
						"Updated Desc",
						category,
						price,
						stock
				);

		Product product = Product.builder()
				.id(id)
				.build();

		Product updated = Product.builder()
				.id(id)
				.name(name)
				.description("Updated Desc")
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
						"Updated Desc",
						category,
						price,
						stock,
						updated.getCreatedAt(),
						updated.getUpdatedAt()
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(product));

		doNothing().when(productMapper)
				.updateEntity(request, product);

		when(productRepository.save(product))
				.thenReturn(updated);

		when(productMapper.toResponseDTO(updated))
				.thenReturn(response);

		ProductResponseDTO result =
				productUpdateService.updateProduct(id, request);

		assertEquals(name, result.name());
		assertEquals(category, result.category());
		assertEquals(price, result.price());

		verify(productRepository).findActiveById(id);
		verify(productMapper).updateEntity(request, product);
		verify(productRepository).save(product);
		verify(productMapper).toResponseDTO(updated);
	}

	static Stream<Arguments> updateProvider() {
		return Stream.of(
				Arguments.of(
						1L, "Laptop", "Electronics",
						new BigDecimal("55000"), 10
				),
				Arguments.of(
						2L, "Phone", "Mobiles",
						new BigDecimal("25000"), 20
				),
				Arguments.of(
						3L, "Keyboard", "Accessories",
						new BigDecimal("1500"), 30
				),
				Arguments.of(
						4L, "Shoes", "Fashion",
						new BigDecimal("2999"), 40
				),
				Arguments.of(
						5L, "Book", "Education",
						new BigDecimal("499"), 50
				)
		);
	}

	// ---------------------------------------------------
	// NOT FOUND TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw ResourceNotFoundException")
	void testUpdateProductNotFound() {

		long id = 999L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Test",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						5
				);

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.empty());

		ResourceNotFoundException ex =
				assertThrows(
						ResourceNotFoundException.class,
						() -> productUpdateService
								.updateProduct(id, request)
				);

		assertEquals(
				"Product not found with ID: 999",
				ex.getMessage()
		);

		verify(productRepository).findActiveById(id);
		verify(productMapper, never())
				.updateEntity(any(), any());
	}

	// ---------------------------------------------------
	// SAVE EXCEPTION TEST
	// ---------------------------------------------------
	@Test
	@DisplayName("Should throw exception when save fails")
	void testUpdateProductSaveException() {

		long id = 2L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Name",
						"Desc",
						"Cat",
						new BigDecimal("100"),
						5
				);

		Product product = Product.builder()
				.id(id)
				.build();

		when(productRepository.findActiveById(id))
				.thenReturn(Optional.of(product));

		doNothing().when(productMapper)
				.updateEntity(request, product);

		when(productRepository.save(product))
				.thenThrow(
						new RuntimeException(
								"Database error"
						)
				);

		RuntimeException ex =
				assertThrows(
						RuntimeException.class,
						() -> productUpdateService
								.updateProduct(id, request)
				);

		assertEquals(
				"Database error",
				ex.getMessage()
		);

		verify(productRepository).save(product);
	}

	// ---------------------------------------------------
	// VERIFY ONLY ONCE
	// ---------------------------------------------------
	@Test
	@DisplayName("Dependencies should be called once")
	void testDependenciesCalledOnce() {

		long id = 7L;

		ProductUpdateRequestDTO request =
				new ProductUpdateRequestDTO(
						"Watch",
						"Smart Watch",
						"Accessories",
						new BigDecimal("5999"),
						5
				);

		Product product = Product.builder().id(id).build();
		Product updated = Product.builder().id(id).build();

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

		doNothing().when(productMapper)
				.updateEntity(request, product);

		when(productRepository.save(product))
				.thenReturn(updated);

		when(productMapper.toResponseDTO(updated))
				.thenReturn(response);

		productUpdateService.updateProduct(id, request);

		verify(productRepository, times(1))
				.findActiveById(id);

		verify(productMapper, times(1))
				.updateEntity(request, product);

		verify(productRepository, times(1))
				.save(product);

		verify(productMapper, times(1))
				.toResponseDTO(updated);

		verifyNoMoreInteractions(
				productRepository,
				productMapper
		);
	}
}
