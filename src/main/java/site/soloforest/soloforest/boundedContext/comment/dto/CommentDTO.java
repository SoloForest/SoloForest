package site.soloforest.soloforest.boundedContext.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	private Long id;
	private Long commentWriter;
	private String commentContents;
	private Long articleId;
	private Boolean secret = false;
	private Long parentId;
	private LocalDateTime commentCreatedTime;
}
