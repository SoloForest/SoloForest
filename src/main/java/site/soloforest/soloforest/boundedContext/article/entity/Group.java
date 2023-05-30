package site.soloforest.soloforest.boundedContext.article.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Group extends Article {

	@NotBlank(message = "인원을 정해줘야 합니다.")
	private int member;

	@NotBlank(message = "시작 날짜를 정해야 합니다.")
	private LocalDateTime startDate;

	@NotBlank(message = "종료 날짜를 정해야 합니다.")
	private LocalDateTime endDate;

	@NotBlank(message = "모임 장소를 정해야 합니다.")
	private String location;

	@NotBlank(message = "회비를 정해주세요.")
	private int money; // 회비
}
