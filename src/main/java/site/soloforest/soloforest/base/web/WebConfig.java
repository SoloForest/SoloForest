package site.soloforest.soloforest.base.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final LoginRejectedInterceptor loginRejectedInterceptor;

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
				.addResourceLocations("file:///" + genFileDirPath + "/images/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginRejectedInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/error/login_rejected"); // 제재당한 사용자는 무조건 로그인이 풀려야 합니다.
	}
}
