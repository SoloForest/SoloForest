package site.soloforest.soloforest.boundedContext.picture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.picture.entity.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long> {
}
