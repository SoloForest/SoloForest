package site.soloforest.soloforest.base.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Getter
public class EventReplyCommentCreate extends ApplicationEvent {
	private final Comment comment;

	public EventReplyCommentCreate(Object source, Comment comment) {
		super(source);
		this.comment = comment;
	}
}
