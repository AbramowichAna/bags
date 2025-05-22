package edu.aseca.bags.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private long expiration;

	private SecretKey key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
	}

	public String extractSubject(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractSubject(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration()
				.before(new Date());
	}
}