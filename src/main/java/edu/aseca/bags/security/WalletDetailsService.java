package edu.aseca.bags.security;

import edu.aseca.bags.domain.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.domain.wallet.WalletNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WalletDetailsService implements UserDetailsService {

	@Autowired
	private WalletRepository walletRepository;

	public UserDetails loadUserByEmail(String email) throws WalletNotFoundException {
		Wallet wallet = walletRepository.findByEmail(new Email(email)).orElseThrow(WalletNotFoundException::new);
		return new WalletDetails(wallet.getEmail(), wallet.getPassword());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return loadUserByEmail(username);
		} catch (WalletNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
	}
}
