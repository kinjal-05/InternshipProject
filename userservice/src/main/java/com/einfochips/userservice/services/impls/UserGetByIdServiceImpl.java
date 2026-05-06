package com.einfochips.userservice.services.impls;

import com.einfochips.userservice.dtos.UserResponseDTO;
import com.einfochips.userservice.models.User;
import com.einfochips.userservice.repositories.UserRepository;
import com.einfochips.userservice.services.UserGetByIdService;
import com.einfochips.userservice.utility.GetActiveUser;
import com.einfochips.userservice.utility.MapToUserResponseDTO;
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
