package site.soloforest.soloforest.base.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import site.soloforest.soloforest.boundedContext.account.entity.Account;

@Getter
public class EventReport extends ApplicationEvent {

	private final Account account;

	public EventReport(Object object, Account account) {
		super(object);
		this.account = account;
	}
}
