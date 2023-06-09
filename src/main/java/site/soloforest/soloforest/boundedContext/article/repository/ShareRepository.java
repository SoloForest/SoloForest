package site.soloforest.soloforest.boundedContext.article.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
	Page<Share> findAllByBoardNumber(int boardNumber, Pageable pageable);
}
