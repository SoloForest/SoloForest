package site.soloforest.soloforest.boundedContext.article.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


	@NotNull(message = "인원을 정해줘야 합니다.")
	private int member;

	@NotNull(message = "시작 날짜를 정해야 합니다.")
	private LocalDateTime startDate;

	@NotNull(message = "종료 날짜를 정해야 합니다.")
	private LocalDateTime endDate;

	@NotBlank(message = "모임 장소를 정해야 합니다.")
	private String location;

