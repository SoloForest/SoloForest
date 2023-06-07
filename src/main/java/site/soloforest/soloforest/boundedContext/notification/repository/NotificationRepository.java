package site.soloforest.soloforest.boundedContext.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByAccountOrderByIdDesc(Account account);
}
