package site.soloforest.soloforest.boundedContext.notification.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String content;
	@CreatedDate
	private LocalDateTime createDate;

	/**
	 * 알림 유형을 나타내는 변수
	 * 0: 댓글 발생
	 * 1: 신고 발생
	 */
	private int event_type;

	/**
	 * comment_id 또는 report_id 저장
	 */
	private Long event_id;

	// 연관관계 맺기
	@OneToOne(mappedBy = "notification", cascade = CascadeType.REMOVE)
	private Comment comment;

	@ManyToOne
	private Account account;
}
