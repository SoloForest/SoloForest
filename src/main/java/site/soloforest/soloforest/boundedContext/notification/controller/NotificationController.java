package site.soloforest.soloforest.boundedContext.notification.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	private final Rq rq;

	@GetMapping("/notification")
	@PreAuthorize("isAuthenticated()")
	public String showNotify(Model model) {
		Account account = rq.getAccount();

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

		if (notification.getEventType() == 0) {
			Comment comment = rq.getCommentById(notification.getEventId());
			if (comment == null) {
				rq.historyBack("이미 삭제된 댓글입니다.");
			}
			Article article = comment.getArticle();
			int page = rq.getPageNumberByComment(comment);

			if (article.getBoardNumber() < 2)
				return "redirect:/article/share/detail/" + article.getId() + "?page=" + page + "#" + "comment_"
					+ comment.getId();

			return "redirect:/article/group/detail/" + article.getId() + "?page=" + page + "#" + "comment_"
				+ comment.getId();
		}

		// TODO : 신고 받은 알림을 클릭했다면 그냥 알림창으로 다시가게
		return rq.redirectWithMsg("/main", "신고된 알림을 클릭하여 메인으로 이동합니다.");
	}

	@PostMapping("/notification/delete")
	@PreAuthorize("isAuthenticated()")
	public String deleteNotification(Model model, @RequestParam Long notificationId) {
		Account account = rq.getAccount();

		Notification notification = notificationService.getNotification(notificationId);
		if (account.getId() != rq.getAccountById(notification.getEventId()).getId()) {
			rq.redirectWithMsg("/main", "본인만 삭제할 수 있습니다.");
		}

		notificationService.delete(notification);

		List<Notification> notifications = notificationService.getNotifications(account);

		model.addAttribute("notifications", notifications);
		return "notification/notification :: #notification-list";
	}

	@PostMapping("/notification/deleteAll")
	@PreAuthorize("isAuthenticated()")
	public String deleteAllNotification(@RequestParam Long accountId) {

		notificationService.deleteAll(accountId);

		return rq.redirectWithMsg("/notification", "전체 삭제 되었습니다.");
	}
}
