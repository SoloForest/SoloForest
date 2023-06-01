package site.soloforest.soloforest.boundedContext.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
}
