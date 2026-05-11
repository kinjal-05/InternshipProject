package com.apigatewayservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
public class JwtUtil {

	private final Key signingKey;

	public JwtUtil(@Value("${jwt.secret}") String secret) {
		this.signingKey = Keys.hmacShaKeyFor(
				Decoders.BASE64.decode(secret)
		);
	}

	// ✅ Validate and extract claims
	public Claims validateAndGetClaims(String token) {
		return Jwts.parser()
				.verifyWith((SecretKey) signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	// ✅ FIXED: Extract numeric userId safely (NOT from subject)
	public long getUserId(String token) {
		Claims claims = validateAndGetClaims(token);

		Object userIdObj = claims.get("userId");

		if (userIdObj == null) {
			throw new JwtException("userId claim is missing in token");
		}

		if (!(userIdObj instanceof Number)) {
			throw new JwtException("Invalid userId type in token");
		}

		return ((Number) userIdObj).longValue();  // ✅ safe
	}

	// ✅ Extract role
	public String getRole(String token) {
		return validateAndGetClaims(token).get("role", String.class);
	}

	// ✅ Optional: extract email (subject)
	public String getEmail(String token) {
		return validateAndGetClaims(token).getSubject();
	}

	// ✅ Validate token
	public boolean isValid(String token) {
		try {
			validateAndGetClaims(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
