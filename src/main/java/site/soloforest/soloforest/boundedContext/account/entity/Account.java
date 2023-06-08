package site.soloforest.soloforest.boundedContext.account.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Account {
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreatedDate
	private LocalDateTime createDate;
	@Column(unique = true)
	@Size(min = 4, max = 16)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(unique = true)
	private String nickname;
	@Column(unique = true)
	private String email;
	private String imageUrl;
	private String address;

	/**
	 * 사용자 권한 코드를 나타내는 변수
	 * 0: 관리자
	 * 1: 일반 사용자
	 * default : 1 (일반 사용자)
	 */
	@Builder.Default
	private int authority = 1;
	@Builder.Default
	private int reported = 0;

	@OneToMany(cascade = {CascadeType.REMOVE})
	@ToString.Exclude
	private List<Notification> Notifications;

	public static class AccountBuilder {
		public AccountBuilder password(String password) {
			this.password = passwordEncoder.encode(password);
			return this;
		}
	}

	public String getNickname() {
		if (this.nickname == null) {
			return "알 수 없는 이용자";
		}
		return this.nickname;
	}

	public void setPassword(String password) {
		this.password = passwordEncoder.encode(password);
	}

	public List<? extends GrantedAuthority> getGrantedAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		grantedAuthorities.add(new SimpleGrantedAuthority("user"));

		if (this.authority == 0) {
			grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
		}

		return grantedAuthorities;
	}
}
