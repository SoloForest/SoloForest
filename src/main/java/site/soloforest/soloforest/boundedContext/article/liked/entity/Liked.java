package site.soloforest.soloforest.boundedContext.article.liked.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liked {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private Account account;
	@ManyToOne(fetch = FetchType.LAZY)
	private Article article;
}
