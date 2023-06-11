package site.soloforest.soloforest.boundedContext.account.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyForm {
	String imageUrl;
	@Size(min = 4, max = 16)
	String password;
	@Size(min = 4, max = 16)
	String passwordCheck;
	@Size(min = 2, max = 32)
	String nickname;
	@Pattern(regexp = "/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$/i", message = "이메일 형식이 아닙니다.")
	String email;
	String address;
}
