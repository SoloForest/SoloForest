package site.soloforest.soloforest.boundedContext.account.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyForm {
	MultipartFile file;
	String password;
	String passwordCheck;
	@Size(min = 2, max = 32)
	String nickname;
	@Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "이메일 형식이 아닙니다.")
	String email;
	String address;
}
