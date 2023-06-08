package site.soloforest.soloforest.boundedContext.notification.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

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

	// 읽은 날짜 표기 -> 안읽음 알림만 모아보기
	private LocalDateTime readDate;

	public boolean isRead() {
		return readDate != null;
	}

	public void markAsRead() {
		readDate = LocalDateTime.now();
	}

	public String getCreateDateAfterStrHuman() {
		return diffFormatHuman(LocalDateTime.now(), getCreateDate());
	}

	public static String diffFormatHuman(LocalDateTime time1, LocalDateTime time2) {
		String suffix = time1.isAfter(time2) ? "전" : "후";

		Period period = Period.between(time1.toLocalDate(), time2.toLocalDate());
		long diff = Math.abs(ChronoUnit.SECONDS.between(time1, time2));

		long diffMonths = period.toTotalMonths();
		long diffYears = period.getYears();
		long diffSeconds = diff % 60; // 초 부분만
		long diffMinutes = diff / (60) % 60; // 분 부분만
		long diffHours = diff / (60 * 60) % 24; // 시간 부분만
		long diffDays = diff / (60 * 60 * 24); // 나머지는 일 부분으로

		StringBuilder sb = new StringBuilder();

		if (diffYears > 0) sb.append(diffYears).append("년 ");
		if (diffMonths > 0) sb.append(diffMonths).append("개월 ");
		if (diffDays > 0) sb.append(diffDays).append("일 ");
		if (diffHours > 0) sb.append(diffHours).append("시간 ");
		if (diffMinutes > 0) sb.append(diffMinutes).append("분 ");
		if (sb.isEmpty()) sb.append("1분 ");

		return sb.append(suffix).toString();
	}

}
