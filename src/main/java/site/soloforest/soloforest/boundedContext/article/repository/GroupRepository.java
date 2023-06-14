package site.soloforest.soloforest.boundedContext.article.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.soloforest.soloforest.boundedContext.article.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

	Optional<Group> findById(Long id);

	@Query("select distinct g "
		+ "from Group g "
		+ "where "
		+ "   (g.subject like %:kw% "
		+ "   or g.content like %:kw%) "
	)
	Page<Group> findGroupBykeyword(@Param("kw") String kw,
		Pageable pageable);
}

