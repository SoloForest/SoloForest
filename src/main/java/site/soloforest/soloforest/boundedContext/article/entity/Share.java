package site.soloforest.soloforest.boundedContext.article.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import site.soloforest.soloforest.boundedContext.picture.entity.Picture;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class Share extends Article {
	@OneToOne
	private Picture picture;
}