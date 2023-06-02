package site.soloforest.soloforest.boundedContext.article.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Share extends Article {
	@Column
	private String imageUrl;
}