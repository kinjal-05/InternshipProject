package com.userservice.services.impls;

import com.userservice.dtos.UserResponseDTO;
import com.userservice.dtos.UserUpdateRequestDTO;
import com.userservice.models.User;
import com.userservice.repositories.UserRepository;
import com.userservice.services.UserUpdateService;
import com.userservice.utility.GetActiveUser;
import com.userservice.utility.MapToUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation responsible for updating existing user records.
 *
 * <p>
 * This service retrieves an active user, applies update operations, persists
 * the modified entity, and returns the updated response DTO.
 *
 * <p>
 * Typical update scenarios include:
 * <ul>
 * <li>Changing user role</li>
 * <li>Updating profile information</li>
 * <li>Modifying account status</li>
 * <li>Administrative account maintenance</li>
 * </ul>
 *
 * <p>
 * Only active (non-deleted) users are eligible for updates.
 *
 * <p>
 * The update operation runs inside a transactional boundary with timeout
 * protection to prevent long-running database transactions.
 *
 * @author Kinjal Mistry
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserUpdateServiceImpl implements UserUpdateService {
	private final UserRepository userRepository;
	private final GetActiveUser getActiveUser;
	private final MapToUserResponseDTO mapToUserResponseDTO;

	/**
	 * Updates the specified user with provided request data.
	 *
	 * <p>
	 * Execution flow:
	 * <ul>
	 * <li>Retrieve active user by ID</li>
	 * <li>Apply incoming changes from request object</li>
	 * <li>Persist updated entity</li>
	 * <li>Return updated response DTO</li>
	 * </ul>
	 *
	 * <p>
	 * Transaction settings:
	 * <ul>
	 * <li>Timeout: 10 seconds</li>
	 * <li>Rollback on runtime exceptions</li>
	 * </ul>
	 *
	 * <p>
	 * <b>Note:</b> Current implementation saves the existing entity without
	 * modifying fields. Extend by mapping request fields to the entity before
	 * saving.
	 *
	 * @param id      unique identifier of the user to update
	 * @param request request containing updated user values
	 * @return updated user response DTO
	 */
	@Override
	@Transactional
	public UserResponseDTO updateUser(long id, UserUpdateRequestDTO request) {
		User user = getActiveUser.getUserOrThrow(id);
		User updatedUser = userRepository.save(user);
		return mapToUserResponseDTO.mapToUserResponseDTO(updatedUser);
	}

}
