package site.soloforest.soloforest.boundedContext.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import site.soloforest.soloforest.boundedContext.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByUsername(String username);

	Optional<Account> findById(Long id);

	Optional<Account> findByEmail(String email);

	Optional<Account> findByNickname(String nickname);

	@Modifying
	@Transactional
	@Query("UPDATE Account a SET a.picture = null WHERE a.picture.id = :pictureId")
	void updatePictureIdToNull(Long pictureId);
}
