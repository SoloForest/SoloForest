package site.soloforest.soloforest.boundedContext.article.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import site.soloforest.soloforest.boundedContext.account.entity.Account;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Article {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Account account;
	@Column(length = 200)
	private String subject;
	@Column(columnDefinition = "TEXT")
	private String content;
	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;
	private int viewed;
	private int liked;
	private int tag;

	/**
	 * 게시판 성격을 알려주는 변수
	 * 0 : 정보 공유성 게시판
	 * 1 : 인원 모집형 게시판 */
	private int boardNumber;
}