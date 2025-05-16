package edu.aseca.bags.appservice;

import edu.aseca.bags.auth.AuthRequest;
import edu.aseca.bags.auth.AuthResponse;
import edu.aseca.bags.domain.wallet.WalletNotFoundException;
import edu.aseca.bags.security.JwtUtil;
import edu.aseca.bags.security.WalletDetailsService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class AuthService {

	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;
	private final WalletDetailsService walletDetailsService;

	public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil, WalletDetailsService service) {
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
		this.walletDetailsService = service;
	}

	public AuthResponse login(@Valid AuthRequest request) throws WalletNotFoundException {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		UserDetails details = walletDetailsService.loadUserByEmail(request.email());
		String token = jwtUtil.generateToken(details);
		return new AuthResponse(token);
	}
}
