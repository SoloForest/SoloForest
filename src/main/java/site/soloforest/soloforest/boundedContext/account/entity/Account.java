package site.soloforest.soloforest.boundedContext.account.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;

@Entity
@Getter
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreatedDate
	private LocalDateTime createDate;
	@Column(unique = true)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(unique = true, nullable = false)
	private String nickname;
	@Column(unique = true, nullable = false)
	private String email;
	private String imageUrl;
	private String address;

	/**
	 * 사용자 권한 코드를 나타내는 변수
	 * 0: 관리자
	 * 1: 일반 사용자
	 */
	private int authority;
	private int reported;

	@OneToMany(mappedBy = "", cascade = {CascadeType.REMOVE})
	@ToString.Exclude
	private List<Notification> Notifications;
}
