package site.soloforest.soloforest.boundedContext.notification.service;

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
}
