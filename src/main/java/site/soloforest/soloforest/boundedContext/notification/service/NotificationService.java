package site.soloforest.soloforest.boundedContext.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationRepository notificationRepository;
	@Transactional
	public void whenCreateComment(Comment comment) {
		String content = comment.getWriter().getUsername() + "님이 회원님의 게시글에 댓글을 남겼습니다.";
		Account account = comment.getArticle().getAccount();

		Notification notification = Notification.builder()
			.content(content)
			.event_type(0)
			.account(account)
			.event_id(comment.getId())
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenReplyCommentCreate(Comment replyComment) {
		String content = replyComment.getWriter().getUsername() + "님이 회원님의 댓글에 댓글을 남겼습니다.";
		Account account = replyComment.getParent().getWriter();

		Notification notification = Notification.builder()
			.content(content)
			.event_type(0)
			.account(account)
			.event_id(replyComment.getId())
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void markAsRead(List<Notification> notifications) {
		notifications
			.stream()
			.filter(notification -> !notification.isRead())
			.forEach(Notification::markAsRead);

		// ToDo : RsData 도입 시 성공 메세지 추가
	}

	public List<Notification> getNotifications(Account account) {
		return notificationRepository.findByAccountOrderByIdDesc(account);
	}

	public Notification getNotification(Long notificationId) {
		return notificationRepository.findById(notificationId).orElse(null);
	}
}
