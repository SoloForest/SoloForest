package site.soloforest.soloforest.base.security;

import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import site.soloforest.soloforest.boundedContext.account.entity.Account;

@Getter
public class AccountAdapter extends User {
	private Long id;

	public AccountAdapter(Account account) {
		super(account.getUsername(), account.getPassword(), account.getGrantedAuthorities());

		this.id = account.getId();
	}
}
