package edu.aseca.bags.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final WalletDetailsService walletDetailsService;

	@Autowired
	public JwtAuthenticationFilter(JwtUtil jwtUtil, WalletDetailsService walletDetailsService) {
		this.jwtUtil = jwtUtil;
		this.walletDetailsService = walletDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		String email = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			email = jwtUtil.extractSubject(jwt);
		}

		try {
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.walletDetailsService.loadUserByEmail(email);
				if (jwtUtil.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		} catch (Exception ignored) {
			filterChain.doFilter(request, response);
		}

		filterChain.doFilter(request, response);
	}
}
