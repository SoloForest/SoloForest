package site.soloforest.soloforest.boundedContext.article.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder(toBuilder = true)
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

	@OneToMany(mappedBy = "article", cascade = {CascadeType.REMOVE})
	@Builder.Default
	@ToString.Exclude
	private List<Comment> comments = new ArrayList<>();

	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;
	private int viewed;
	private int likes;
	private int tag;

	/**
	 * 게시판 성격을 알려주는 변수
	 * 0 : 커뮤니티 게시판
	 * 1 : 프로그램 게시판
	 * 2 : 인원모집 게시판 **/
	private int boardNumber;

	public String getBoardType() {
		return switch (boardNumber) {
			case 1 -> "program";
			case 2 -> "group";
			default -> "community";
		};
	}

	public void updateViewed() {
		this.viewed++;
	}

	public void upLikes() {
		++likes;
	}

	public void downLikes() {
		--likes;
	}
}