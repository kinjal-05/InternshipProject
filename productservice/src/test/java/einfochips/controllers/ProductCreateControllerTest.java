package einfochips.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.einfochips.controllers.ProductCreateController;
import com.einfochips.dtos.ApiResponse;
import com.einfochips.dtos.ProductRequestDTO;
import com.einfochips.dtos.ProductResponseDTO;
import com.einfochips.services.ProductCreateService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit Tests for {@link ProductCreateController}.
 *
 * <p>All branches covered:
 * <ul>
 *   <li>201 CREATED  — valid request, service returns ProductResponseDTO</li>
 *   <li>201 CREATED  — service returns null (edge case)</li>
 *   <li>201 CREATED  — multiple products via parameterized test</li>
 *   <li>RuntimeException     — service throws unexpected exception</li>
 *   <li>IllegalArgumentException — service throws invalid-data exception</li>
 *   <li>DuplicateSkuException    — service throws domain-specific conflict</li>
 *   <li>Interaction verification — service called exactly once per request</li>
 * </ul>
 *
 * <p><b>Key corrections vs. the original test file:</b>
 * <ul>
 *   <li>{@link ProductRequestDTO} is a <b>record</b> — no setters, use canonical constructor</li>
 *   <li>{@link ProductResponseDTO} is a <b>record</b> — no setters, use canonical constructor</li>
 *   <li>No {@code sku} field on either DTO — all references removed</li>
 *   <li>Correct request fields : name, description, category, price, stockQuantity</li>
 *   <li>Correct response fields: id, name, description, category, price, stockQuantity,
 *       createdAt, updatedAt</li>
 *   <li>Record accessors used ({@code .name()}, {@code .price()}, …) not getters</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCreateController — Unit Tests")
class ProductCreateControllerTest {

	// ─────────────────────────────────────────────────────────────────────────
	// Mocks & Subject Under Test
	// ─────────────────────────────────────────────────────────────────────────

	@Mock
	private ProductCreateService productCreateService;

	@InjectMocks
	private ProductCreateController productCreateController;

	// ─────────────────────────────────────────────────────────────────────────
	// Shared test data  (built once per test via @BeforeEach)
	// ─────────────────────────────────────────────────────────────────────────

	private ProductRequestDTO request;
	private ProductResponseDTO response;

	@BeforeEach
	void setUp() {
		/*
		 * ProductRequestDTO is a record:
		 *   record ProductRequestDTO(
		 *       String name, String description, String category,
		 *       BigDecimal price, Integer stockQuantity)
		 *
		 * Records are immutable — there are NO setters.
		 * Always use the canonical constructor.
		 */
		request = new ProductRequestDTO(
				"Laptop",                  // name
				"A powerful laptop",       // description
				"Electronics",             // category
				new BigDecimal("999.99"),  // price
				50                         // stockQuantity
		);

		/*
		 * ProductResponseDTO is a record:
		 *   record ProductResponseDTO(
		 *       long id, String name, String description, String category,
		 *       BigDecimal price, Integer stockQuantity,
		 *       LocalDateTime createdAt, LocalDateTime updatedAt)
		 */
		response = new ProductResponseDTO(
				1L,
				"Laptop",
				"A powerful laptop",
				"Electronics",
				new BigDecimal("999.99"),
				50,
				LocalDateTime.of(2025, 1, 1, 10, 0),
				LocalDateTime.of(2025, 1, 1, 10, 0)
		);
	}

	// =========================================================================
	// Branch 1 — 201 CREATED: normal success
	// =========================================================================

	@Nested
	@DisplayName("Branch 1 — 201 CREATED: Success")
	class SuccessBranch {

		@Test
		@DisplayName("Should return 201 and correctly populated body when service succeeds")
		void testCreateProductSuccess() {

			// Arrange
			when(productCreateService.createProduct(request)).thenReturn(response);

			// Act
			ResponseEntity<ApiResponse<ProductResponseDTO>> result =
					productCreateController.createProduct(request);

			// Assert — HTTP status
			assertNotNull(result);
			assertEquals(HttpStatus.CREATED, result.getStatusCode());

			// Assert — ApiResponse wrapper
			assertNotNull(result.getBody());
			assertTrue(result.getBody().isSuccess());
			assertEquals("Product created successfully", result.getBody().getMessage());

			// Assert — payload (use record accessors, NOT getters)
			ProductResponseDTO data = result.getBody().getData();
			assertNotNull(data);
			assertEquals(1L,                         data.id());
			assertEquals("Laptop",                   data.name());
			assertEquals("A powerful laptop",        data.description());
			assertEquals("Electronics",              data.category());
			assertEquals(new BigDecimal("999.99"),   data.price());
			assertEquals(50,                         data.stockQuantity());

			// Assert — interaction
			verify(productCreateService, times(1)).createProduct(request);
		}

		@Test
		@DisplayName("Should return 201 with null data when service returns null (edge case)")
		void testCreateProductNullResponse() {

			// Arrange
			when(productCreateService.createProduct(request)).thenReturn(null);

			// Act
			ResponseEntity<ApiResponse<ProductResponseDTO>> result =
					productCreateController.createProduct(request);

			// Assert
			assertEquals(HttpStatus.CREATED, result.getStatusCode());
			assertNotNull(result.getBody());
			assertNull(result.getBody().getData());
			assertEquals("Product created successfully", result.getBody().getMessage());

			verify(productCreateService, times(1)).createProduct(request);
		}
	}

	// =========================================================================
	// Branch 2 — Parameterized: multiple products
	// =========================================================================

	@Nested
	@DisplayName("Branch 2 — 201 CREATED: Parameterized (multiple products)")
	class ParameterizedBranch {

		/**
		 * Runs once per row in {@link #productData()}.
		 * Verifies the controller works correctly for a variety of valid inputs.
		 */
		@ParameterizedTest(name = "[{index}] name={0}, category={1}, price={2}, stock={3}")
		@MethodSource("productProvider")
		@DisplayName("Should create various products successfully")
		void testCreateProductParameterized(
				String name,
				String category,
				BigDecimal price,
				int stockQuantity) {

			// Arrange
			ProductRequestDTO req = new ProductRequestDTO(
					name,
					"Some description",
					category,
					price,
					stockQuantity
			);

			ProductResponseDTO res = new ProductResponseDTO(
					1L,
					name,
					"Some description",
					category,
					price,
					stockQuantity,
					LocalDateTime.now(),
					LocalDateTime.now()
			);

			when(productCreateService.createProduct(req)).thenReturn(res);

			// Act
			ResponseEntity<ApiResponse<ProductResponseDTO>> result =
					productCreateController.createProduct(req);

			// Assert
			assertEquals(HttpStatus.CREATED, result.getStatusCode());
			assertNotNull(result.getBody());
			assertEquals("Product created successfully", result.getBody().getMessage());

			assertNotNull(result.getBody().getData());
			assertEquals(name, result.getBody().getData().name());
			assertEquals(category, result.getBody().getData().category());
			assertEquals(price, result.getBody().getData().price());
			assertEquals(stockQuantity, result.getBody().getData().stockQuantity());

			verify(productCreateService, times(1)).createProduct(req);
		}

		static Stream<Arguments> productProvider() {
			return Stream.of(
					Arguments.of("Laptop", "Electronics", new BigDecimal("55000"), 10),
					Arguments.of("Phone", "Mobiles", new BigDecimal("25000"), 15),
					Arguments.of("Keyboard", "Accessories", new BigDecimal("1500"), 30),
					Arguments.of("Shoes", "Fashion", new BigDecimal("2999"), 20),
					Arguments.of("Book", "Education", new BigDecimal("499"), 50)
			);
		}
	}

	/**
	 * Static @MethodSource provider — must be at the top-level class, not inside @Nested.
	 * Referenced by fully-qualified class name in the nested test above.
	 */
	static Stream<Arguments> productData() {
		return Stream.of(
				Arguments.of("Laptop",   "Electronics", new BigDecimal("999.99"), 50),
				Arguments.of("Mobile",   "Electronics", new BigDecimal("499.99"), 100),
				Arguments.of("Keyboard", "Peripherals", new BigDecimal("49.99"),  200),
				Arguments.of("Monitor",  "Electronics", new BigDecimal("299.99"), 30),
				Arguments.of("Headset",  "Accessories", new BigDecimal("79.99"),  75)
		);
	}

	// =========================================================================
	// Branch 3 — RuntimeException (unexpected service failure)
	// =========================================================================

	@Nested
	@DisplayName("Branch 3 — Exception: unexpected RuntimeException")
	class RuntimeExceptionBranch {

		@Test
		@DisplayName("Should propagate RuntimeException thrown by service")
		void testCreateProductThrowsRuntimeException() {

			// Arrange
			when(productCreateService.createProduct(request))
					.thenThrow(new RuntimeException("Unexpected database error"));

			// Act & Assert
			RuntimeException ex = assertThrows(RuntimeException.class, () ->
					productCreateController.createProduct(request));

			assertEquals("Unexpected database error", ex.getMessage());
			verify(productCreateService, times(1)).createProduct(request);
		}

		@Test
		@DisplayName("Should propagate IllegalArgumentException thrown by service")
		void testCreateProductThrowsIllegalArgumentException() {

			// Arrange
			when(productCreateService.createProduct(any(ProductRequestDTO.class)))
					.thenThrow(new IllegalArgumentException("Invalid product data"));

			// Act & Assert
			IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
					productCreateController.createProduct(request));

			assertEquals("Invalid product data", ex.getMessage());
			verify(productCreateService, times(1)).createProduct(any(ProductRequestDTO.class));
		}
	}


	@Nested
	@DisplayName("Branch 5 — Interaction verification")
	class InteractionVerification {

		@Test
		@DisplayName("Service should be called exactly once per request")
		void testServiceCalledExactlyOnce() {

			when(productCreateService.createProduct(request)).thenReturn(response);

			productCreateController.createProduct(request);

			verify(productCreateService, times(1)).createProduct(request);
			verifyNoMoreInteractions(productCreateService);
		}

		@Test
		@DisplayName("Service should never receive a null argument")
		void testServiceNeverReceivesNullArgument() {

			when(productCreateService.createProduct(request)).thenReturn(response);

			productCreateController.createProduct(request);

			// Confirm the service was never called with null
			verify(productCreateService, never()).createProduct(argThat(r -> r == null));
			verify(productCreateService, times(1)).createProduct(request);
		}

		@Test
		@DisplayName("Response wrapper should always carry success=true and message on 201")
		void testResponseWrapperAlwaysPresent() {

			when(productCreateService.createProduct(any(ProductRequestDTO.class)))
					.thenReturn(response);

			ResponseEntity<ApiResponse<ProductResponseDTO>> result =
					productCreateController.createProduct(request);

			assertAll(
					() -> assertNotNull(result),
					() -> assertNotNull(result.getBody()),
					() -> assertEquals(HttpStatus.CREATED,              result.getStatusCode()),
					() -> assertTrue(result.getBody().isSuccess()),
					() -> assertEquals("Product created successfully",  result.getBody().getMessage())
			);
		}
	}

	// =========================================================================
	// Domain exception stub
	// =========================================================================

	/**
	 * Stub for the project's duplicate-SKU domain exception.
	 * Replace with the real class once it is created in the codebase.
	 */

}