package site.soloforest.soloforest.boundedContext.article.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.soloforest.soloforest.boundedContext.article.entity.Share;

public interface ShareRepository extends JpaRepository<Share, Long> {
	Page<Share> findAllByBoardNumber(int boardNumber, Pageable pageable);

	@Query("select distinct s "
		+ "from Share s "
		+ "where "
		+ "   (s.subject like %:kw% "
		+ "   or s.content like %:kw%) "
		+ "   and s.boardNumber = :boardNumber")
	Page<Share> findByBoardNumberAndKeyword(@Param("boardNumber") int boardNumber, @Param("kw") String kw,
		Pageable pageable);
}
