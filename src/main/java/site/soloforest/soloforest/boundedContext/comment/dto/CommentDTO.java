package site.soloforest.soloforest.boundedContext.comment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDTO {
	private Long id;
	private String commentWriter;
	private String commentContents;
	private Long boardId;

}
