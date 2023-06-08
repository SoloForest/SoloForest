package site.soloforest.soloforest.boundedContext.notification.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	private final AccountService accountService;

	private final CommentService commentService;

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

	@PostMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String redirectArticle(@RequestParam Long notificationId) {
		Notification notification = notificationService.getNotification(notificationId);

		if(notification.getEvent_type() == 0) {
			Comment comment = commentService.getComment(notification.getEvent_id());
			Article article = comment.getArticle();
			int boardtype = article.getBoardNumber();

			// TODO : boardtype에 따라 해당 게시글 상세 페이지 이동 + 댓글 앵커
		}

		// TODO : 신고 받은 알림을 클릭했다면 그냥 알림창으로 다시가게
		return "redirect:/main";
	}

}
