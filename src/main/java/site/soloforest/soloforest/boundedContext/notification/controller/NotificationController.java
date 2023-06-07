package site.soloforest.soloforest.boundedContext.notification.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.service.ArticleService;
import site.soloforest.soloforest.boundedContext.notification.dto.NotificationDTO;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	private final ArticleService articleService;

	private final AccountService accountService;

	@GetMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String showNotify(Model model, Principal principal){
		Account account = accountService.findByUsername(principal.getName());

		List<Notification> notifications = notificationService.getNotifications(account);

		model.addAttribute("notifications", notifications);
		return "notification/notification";
	}

	@PostMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String redirectArticle(Model model, @RequestBody @ModelAttribute NotificationDTO notificationDTO){
		// 알림 읽음 처리
		Long notificationId = notificationDTO.getNotificationId();
		Notification notification = notificationService.getNotification(notificationId);
		notification.markAsRead();

		// 리다이렉트 할 게시글 정보 가져오기
		Long articleId = notificationDTO.getArticleId();
		Article article = articleService.getArticle(articleId);

		// TODO : 게시판 + 게시글 Id로 상세 페이지 이동
		switch (article.getBoardNumber()){
			case 0 -> {
				return "redirect:/main";
			}
			case 1 -> {
				return "redirect:/main";
			}
			case 2 -> {
				return "redirect:/main";
			}
			default -> {
				return "redirect:/main";
			}
		}
	}
}
