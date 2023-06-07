package site.soloforest.soloforest.boundedContext.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationDTO {
	private Boolean readIt;
	private Long articleId;
	private Long notificationId;

	// 커스텀 생성자 추가
	@JsonCreator
	public NotificationDTO(@JsonProperty("readIt") Boolean readIt, @JsonProperty("articleId") Long articleId, @JsonProperty("notificationId") Long notificationId) {
		this.readIt = readIt;
		this.articleId = articleId;
		this.notificationId = notificationId;
	}
}
