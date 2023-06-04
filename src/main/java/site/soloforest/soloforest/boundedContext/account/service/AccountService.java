package site.soloforest.soloforest.boundedContext.account.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	public Account singup(AccountDTO dto) {
		Account account = Account.builder()
			.username(dto.getUsername())
			.password(dto.getPassword())
			.nickname(dto.getNickname())
			.email(dto.getEmail())
			.address(dto.getAddress())
			.build();
		Account savedAccount = accountRepository.save(account);
		return savedAccount;
	}

	public Account create(Account account) {
		return accountRepository.save(account);
	}
}
