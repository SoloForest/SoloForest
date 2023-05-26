package site.soloforest.soloforest.boundedContext.article.entity;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Group extends Article {

	@Column(unique = true)
	private int member;

	@Column(unique = true)
	private LocalDateTime startDate;

	@Column(unique = true)
	private LocalDateTime endDate;

	@Column(unique = true)
	private String location;

	@Column(unique = true)
	private int money; // 회비
}
