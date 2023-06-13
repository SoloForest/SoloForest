package site.soloforest.soloforest.base.email;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailDTO {
	private String to;
	private String subject;
	private String message;
}
