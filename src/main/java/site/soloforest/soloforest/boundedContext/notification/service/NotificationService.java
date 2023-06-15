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
		if (comment.getWriter().getId() != comment.getArticle().getAccount().getId()) {
			String content = comment.getWriter().getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.";
			Long accountId = comment.getArticle().getAccount().getId();
			Notification notification = Notification.builder()
				.content(content)
				.eventType(0)
				.eventId(comment.getId())
				.accountId(accountId)
				.readDate(null)
				.build();
			notificationRepository.save(notification);
		}
	}

	@Transactional
	public void whenReplyCommentCreate(Comment replyComment) {
		//부모 댓글과 자식 댓글 작성자가 다르다 -> 다른사람이 댓글에 답글 남겨준 거니 알림
		if (replyComment.getWriter().getId() != replyComment.getParent().getWriter().getId()) {
			String content = replyComment.getWriter().getNickname() + "님이 회원님의 댓글에 댓글을 남겼습니다.";
			// 부모 댓글쓴 사용자에게 알림 가게 설정
			Long accountId = replyComment.getParent().getWriter().getId();

			Notification notification = Notification.builder()
				.content(content)
				.eventType(0)
				.eventId(replyComment.getId())
				.accountId(accountId)
				.readDate(null)
				.build();

			notificationRepository.save(notification);
		}
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
		return notificationRepository.findByAccountIdOrderByIdDesc(account.getId());
	}

	public Notification getNotification(Long notificationId) {
		return notificationRepository.findById(notificationId).orElse(null);
	}

	public void whenReportSubmit(Account account) {
		String content = "누군가 당신을 신고하였습니다. 누적 횟수 3회가 되면 3일간 로그인이 제한되니 주의해주세요(현재 %d회)".formatted(account.getReported());
		Notification notification = Notification.builder()
			.content(content)
			.eventType(1)
			.eventId(account.getId())
			.accountId(account.getId())
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void delete(Notification notification) {
		notificationRepository.delete(notification);
	}

	@Transactional
	public void deleteAll(Long accountId) {
		notificationRepository.deleteAllByAccountId(accountId);
	}

	public boolean countUnreadNotifications(Long accountId) {
		return notificationRepository.countByAccountIdAndReadDateIsNull(accountId) > 0;
	}
}
