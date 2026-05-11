package com.productservice.services;

/**
 * Service interface for Product Delete operation.
 *
 * <p>
 * Defines the contract for soft-deleting an existing active product.
 */
public interface ProductDeleteService {

	/**
	 * Soft-delete an active product by its ID.
	 *
	 * <p>
	 * Sets {@code isDeleted = true} and records {@code deletedTimestamp}. The
	 * product row is NOT physically removed from the database.
	 *
	 * @param id ID of the product to delete
	 */
	void deleteProduct(long id);

}
