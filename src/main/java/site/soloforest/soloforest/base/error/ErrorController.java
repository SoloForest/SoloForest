package site.soloforest.soloforest.base.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/error")
public class ErrorController {
	@GetMapping("/404")
	public String notFound(HttpServletResponse response) {
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "/error/404";
	}

	@GetMapping("/403")
	public String forbidden(HttpServletResponse response) {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		return "/error/403";
	}
}
