package com.einfochips.services.impls;

import com.einfochips.dtos.UserChangePasswordRequestDTO;
import com.einfochips.exceptions.ResourceNotFoundException;
import com.einfochips.models.User;
import com.einfochips.repositories.UserRepository;
import com.einfochips.services.UserChangePasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserChangePasswordServiceImpl implements UserChangePasswordService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Changes the password of the currently authenticated user.
	 *
	 * Transactional Behavior: - Uses READ_COMMITTED isolation to avoid dirty reads.
	 * - Rolls back for any exception to maintain data consistency.
	 *
	 * Flow: - Fetch authenticated user from security context. - Validate old
	 * password. - Encode and update new password.
	 *
	 * Security: - Prevents password update if old password is incorrect.
	 */
	@Override
	@Transactional
	public void changePassword(UserChangePasswordRequestDTO request) {
		String loggedInEmail = getCurrentUserEmail();
		User user = userRepository.findByEmail(loggedInEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loggedInEmail));

		if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
			throw new BadCredentialsException("Old password is incorrect");
		}
		user.setPassword(passwordEncoder.encode(request.newPassword()));

		userRepository.save(user);

	}

	/**
	 * Retrieves the currently authenticated user's email from SecurityContext.
	 *
	 * Security: - Ensures user is authenticated before accessing context. - Throws
	 * exception for anonymous or unauthenticated access.
	 */
	private static String getCurrentUserEmail() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			throw new BadCredentialsException("User is not authenticated");
		}
		return auth.getName();
	}
}
