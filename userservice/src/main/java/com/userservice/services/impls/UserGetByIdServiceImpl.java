package com.userservice.services.impls;

import com.userservice.dtos.UserResponseDTO;
import com.userservice.models.User;
import com.userservice.repositories.UserRepository;
import com.userservice.services.UserGetByIdService;
import com.userservice.utility.GetActiveUser;
import com.userservice.utility.MapToUserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGetByIdServiceImpl implements UserGetByIdService {
	private final UserRepository userRepository;
	private final GetActiveUser getActiveUser;
	private final MapToUserResponseDTO mapToUserResponseDTO;

	/**
	 * Fetches a user by ID.
	 *
	 * Transactional Behavior: - Read-only transaction ensures no accidental
	 * modifications.
	 *
	 * Notes: - Only active (non-deleted) users are returned.
	 */
	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(long id) {
		User user = getActiveUser.getUserOrThrow(id);
		return mapToUserResponseDTO.mapToUserResponseDTO(user);
	}
}
