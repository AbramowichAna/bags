package edu.aseca.bags.testutil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTestUtil {

	private final Key secretKey;

	public JwtTestUtil(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateExpiredToken(String email) {
		Date now = new Date();
		Date expiredAt = new Date(now.getTime() - 60_000); // Hace 1 minuto

		return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiredAt)
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}

	public String generateValidToken(String email, long durationMillis) {
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + durationMillis);

		return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiresAt)
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}
}
