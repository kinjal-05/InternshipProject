package com.einfochips.userservice.services.impls;

import com.einfochips.userservice.dtos.UserLoginRequestDTO;
import com.einfochips.userservice.dtos.UserLoginResponseDTO;
import com.einfochips.userservice.models.User;
import com.einfochips.userservice.security.CustomUserDetails;
import com.einfochips.userservice.security.JwtService;
import com.einfochips.userservice.services.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	/**
	 * Authenticates user credentials and generates JWT token.
	 *
	 * Flow: - Delegates authentication to AuthenticationManager. - On success,
	 * extracts authenticated user details. - Generates JWT token for stateless
	 * authentication.
	 *
	 * Security: - Throws BadCredentialsException if authentication fails. - No
	 * transaction required (read + auth operation only).
	 */
	@Override
	@Transactional
	public UserLoginResponseDTO login(UserLoginRequestDTO request) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(customUserDetails);
		User user = customUserDetails.getUser();
		return new UserLoginResponseDTO(user.getId(), user.getEmail(), user.getRole(), token, "Login Successful");
	}
}
