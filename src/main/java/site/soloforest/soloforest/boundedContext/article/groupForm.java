package site.soloforest.soloforest.boundedContext.article;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class groupForm {
	private Long id;
	private String subject;
	private String content;
	private int member;
	private int money;
	private String location;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public groupForm(String subject, String content) {
		this.subject = subject;
		this.content = content;
	}
}