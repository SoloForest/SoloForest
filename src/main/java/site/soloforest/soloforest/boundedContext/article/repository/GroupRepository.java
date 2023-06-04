package site.soloforest.soloforest.boundedContext.article.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.soloforest.soloforest.boundedContext.article.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
	Optional<Group> findById(Long id);
}
