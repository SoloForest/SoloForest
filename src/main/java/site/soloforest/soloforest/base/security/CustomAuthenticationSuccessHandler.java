package site.soloforest.soloforest.base.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final AccountRepository accountRepository;
	private RequestCache requestCache = new HttpSessionRequestCache(); // 로그인페이지 이동 전 요청 저장되어있음

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String targetUrl = "/main";

		if (savedRequest != null) {
			targetUrl = savedRequest.getRedirectUrl();
		}

		Account account = accountRepository.findById(
			((AccountAdapter)authentication.getPrincipal()).getId()).orElse(null);
		if (account != null && account.getLoginRejectedDeadline() != null &&
			account.getLoginRejectedDeadline().isAfter(LocalDateTime.now())) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			SecurityContextHolder.clearContext();
			response.sendRedirect("/error/403");
		} else {
			response.sendRedirect(targetUrl);
		}

	}
}
