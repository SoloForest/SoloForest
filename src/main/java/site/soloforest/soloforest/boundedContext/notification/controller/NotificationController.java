package site.soloforest.soloforest.boundedContext.notification.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String showNotify(Model model){

		//
		// List<Notification> notificationList =notificationService.findByAccount()
		return "notification/notification";
	}
}
