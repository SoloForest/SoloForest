package site.soloforest.soloforest.boundedContext.article.bookmark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.bookmark.entity.Bookmark;
import site.soloforest.soloforest.boundedContext.article.entity.Article;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	void deleteByArticleAndAccount(Article article, Account account);

	List<Bookmark> findAllByAccount(Account account);

	List<Bookmark> findAllByArticle(Article article);

	long countByArticleAndAccount(Article articlee, Account account);
}
