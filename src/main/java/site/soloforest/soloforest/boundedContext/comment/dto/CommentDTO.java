package site.soloforest.soloforest.boundedContext.comment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentDTO {
	private Long id;

	private Long commentWriter;
	private String commentContents;
	private Long articleId;
	private Boolean secret = false;
	private Long parentId;
	private LocalDateTime commentCreatedTime;

	@JsonCreator
	public CommentDTO(@JsonProperty("articleId") Long articleId,
		@JsonProperty("commentContents") String commentContents, @JsonProperty("secret") boolean secret,
		@JsonProperty("parentId") Long parentId) {
		this.commentContents = commentContents;
		this.secret = secret;
		this.articleId = articleId;
		this.parentId = parentId;
	}

}
