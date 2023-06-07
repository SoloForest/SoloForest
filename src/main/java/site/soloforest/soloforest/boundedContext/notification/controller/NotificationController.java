package site.soloforest.soloforest.boundedContext.notification.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	private final AccountService accountService;

	@GetMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String showNotify(Model model, Principal principal) {
		Account account = accountService.findByUsername(principal.getName());

		List<Notification> notifications = notificationService.getNotifications(account);
		// 모두 읽기 처리
		notificationService.markAsRead(notifications);

		model.addAttribute("notifications", notifications);
		return "notification/notification";
	}

}
