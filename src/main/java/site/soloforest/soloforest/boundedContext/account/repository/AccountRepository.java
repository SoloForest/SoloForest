package site.soloforest.soloforest.boundedContext.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByUsername(String username);

	Optional<Account> findById(Long id);

	Optional<Account> findByEmail(String email);
}
