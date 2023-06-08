package site.soloforest.soloforest.boundedContext.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
	@NotBlank
	@Size(min = 4, max = 16)
	String username;
	@NotBlank
	@Size(min = 4, max = 16)
	String password;
	@NotBlank
	@Size(min = 4, max = 16)
	String passwordCheck;
	@NotBlank
	@Size(min = 2, max = 32)
	String nickname;
	@NotBlank
	String email;
	String address;
}
