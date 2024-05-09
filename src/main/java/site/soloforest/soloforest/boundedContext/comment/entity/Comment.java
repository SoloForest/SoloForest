package site.soloforest.soloforest.boundedContext.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;

@Entity
@Getter
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Account writer;

	@ManyToOne
	private Article article;

	@ToString.Exclude
	@ManyToOne
	private Comment parent;

	// orphanRemoval로 대댓글과 연관관계 끊어지면 삭제되게 설정
	// 영속성 컨텍스트내 연관 관계 제거 -> 커밋 시 DB에 반영 -> DB 조회 시 최신 상태 반영
	// 삭제 요청 처리 메서드에서 대댓글을 삭제하고, 다시 조회할 때 한 URI 요청 내에서 처리하므로 캐싱된 데이터를 사용
	// 캐싱된 데이터 자체를 수정해주기 때문에 이 옵션을 선택이 아닌 필수
	@OneToMany(mappedBy = "parent", orphanRemoval = true)
	@ToString.Exclude
	@Builder.Default // 빌더패턴 리스트시 초기화
	private List<Comment> children = new ArrayList<>();

	private String content;

	@CreatedDate
	private LocalDateTime createDate;

	private LocalDateTime modifyDate;

	@Builder.Default
	private Boolean secret = false;
	// 삭제 여부 나타내는 속성 추가
	@Builder.Default
	private Boolean deleted = false;
	@OneToOne(fetch = FetchType.LAZY)
	private Notification notification;

	@PrePersist
	public void prePersist() {
		this.modifyDate = null;
	}

	public void deleteParent() {
		deleted = true;
	}

	// 타임리프에서 비밀 댓글이면 댓글의 내용이 안보이게 하기 위함
	public boolean isSecret() {
		return this.secret == true;
	}

	public boolean isDeleted() {
		return this.deleted == true;
	}

}
