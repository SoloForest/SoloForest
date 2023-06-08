package site.soloforest.soloforest.boundedContext.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentDTO {
	private String commentWriter;
	private String commentContents;

	@JsonCreator
	public CommentDTO(@JsonProperty("commentWriter") String commentWriter, @JsonProperty("commentContents") String commentContents) {
		this.commentWriter = commentWriter;
		this.commentContents = commentContents;
	}

}
