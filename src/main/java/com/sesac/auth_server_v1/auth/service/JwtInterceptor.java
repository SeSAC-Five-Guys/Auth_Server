package com.sesac.auth_server_v1.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sesac.auth_server_v1.common.exception.ErrorStatus;
import com.sesac.auth_server_v1.common.exception.ServiceLogicException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
	@Value("${variable.jwt.cookieHeader}")
	private String cookieHeader;

	private final JwtUtils jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieHeader)) {
					String accessToken = cookie.getValue();
					if (jwtService.verifyToken(accessToken)) {
						return true;
					}
				}
			}
		} throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
	}
}
