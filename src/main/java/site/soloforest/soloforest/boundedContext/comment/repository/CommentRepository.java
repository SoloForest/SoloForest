package site.soloforest.soloforest.boundedContext.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findById(Long id);

	List<Comment> findAllByArticle(Article article);

	Page<Comment> findAllByArticle(Article article, Pageable pageable);
}
