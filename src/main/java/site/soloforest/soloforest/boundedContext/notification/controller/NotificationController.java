package site.soloforest.soloforest.boundedContext.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	@GetMapping("/notification")
	public String showNotify(){
		return "notification/notification";
	}
}
