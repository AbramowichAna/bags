package edu.aseca.bags.security;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record WalletDetails(Email email, Password password) implements UserDetails {
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return password().password();
	}

	@Override
	public String getUsername() {
		return email().address();
	}
}
