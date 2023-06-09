package site.soloforest.soloforest.boundedContext.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import net.minidev.json.annotate.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
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

	@OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE})
	@ToString.Exclude
	@Builder.Default // 빌더패턴 리스트시 초기화
	private List<Comment> children = new ArrayList<>();

	private String content;

	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;

	private Boolean secret;
	// 삭제 여부 나타내는 속성 추가
	// "삭제되었습니다" 라고 하기에는 저렇게 작성하는 사용자가 있다면 댓글 삭제된 것으로 처리될 듯
	private Boolean isDeleted;
	@OneToOne
	private Notification notification;

	public void deleteParent(){
		this.content = "삭제되었습니다.";
	}

	// 타임리프에서 비밀 댓글이면 댓글의 내용이 안보이게 하기 위함
	// TODO : 템플릿에서 th:if(비밀글) => th:text = "비밀 댓글입니다."
	//  + (사용자 id = 작성자 or admin or 게시글 작성자만 보이게 타임리프 조건)
 	public boolean isSecret() {
		return this.secret == true;
	}

}
