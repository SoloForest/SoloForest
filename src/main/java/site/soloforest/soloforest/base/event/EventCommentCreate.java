package site.soloforest.soloforest.base.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Getter
public class EventCommentCreate extends ApplicationEvent {
	private final Comment comment;

	public EventCommentCreate(Object source, Comment comment) {
		super(source);
		this.comment = comment;

	}
}
