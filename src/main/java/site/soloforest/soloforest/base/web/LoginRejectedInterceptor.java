package site.soloforest.soloforest.base.web;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.security.AccountAdapter;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class LoginRejectedInterceptor implements HandlerInterceptor {
	private final AccountRepository accountRepository;
	private final HttpSession session;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// 로그인한 사용자인 경우 (principal 객체가 'anonymousUser'일 경우, 비 로그인 상태)
		if (principal instanceof AccountAdapter) {
			Account account = accountRepository.findById(((AccountAdapter)principal).getId())
				.get(); // 현재 로그인한 account id로 최신 엔티티 불러오기
			// 지금 시간이 로그인 거부 기간 안에 있는지 확인
			if (account.getLoginRejectedDeadline() != null
				&& account.getLoginRejectedDeadline().isAfter(LocalDateTime.now())) {
				if (session != null) {
					session.invalidate();
				}
				session.setAttribute("loginRejectedDeadline", account.getLoginRejectedDeadline());
				SecurityContextHolder.clearContext(); // 인증 정보 초기화
				response.sendRedirect("/error/login_rejected"); // 로그인 거부 에러 페이지로 리다이렉트

				return false;
			}
		}

		return true;
	}
}
