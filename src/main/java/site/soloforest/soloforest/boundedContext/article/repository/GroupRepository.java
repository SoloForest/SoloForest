package site.soloforest.soloforest.boundedContext.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
