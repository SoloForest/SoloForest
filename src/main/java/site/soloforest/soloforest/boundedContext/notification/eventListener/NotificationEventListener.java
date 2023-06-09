package site.soloforest.soloforest.boundedContext.notification.eventListener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.event.EventCommentCreate;
import site.soloforest.soloforest.base.event.EventReplyCommentCreate;
import site.soloforest.soloforest.base.event.EventReport;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
@Transactional
public class NotificationEventListener {
	private final NotificationService notificationService;

	@EventListener
	public void listen(EventCommentCreate event) {
		notificationService.whenCreateComment(event.getComment());
	}

	@EventListener
	public void listen(EventReplyCommentCreate event) {
		notificationService.whenReplyCommentCreate(event.getComment());
	}

	@EventListener
	public void listen(EventReport event) {
		notificationService.whenReportSubmit(event.getAccount());
	}

}
