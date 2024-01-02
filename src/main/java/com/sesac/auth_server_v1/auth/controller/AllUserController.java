package com.sesac.auth_server_v1.auth.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sesac.auth_server_v1.auth.service.JwtUtils;
import com.sesac.auth_server_v1.auth.service.RedisUtils;
import com.sesac.auth_server_v1.common.dto.ResDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authorization/all")
public class AllUserController {
	@Value("${variable.jwt.cookieHeader}")
	private String cookieHeader;

	@Value("${variable.clientAddr")
	private String clientAddr;

	private final JwtUtils jwtUtils;

	private final RedisUtils redisUtils;
	@GetMapping("/member")
	public ResponseEntity<ResDto> authorizationAllUser(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{
		notAuthAllUserRedirect(httpServletResponse, httpServletRequest);
		return new ResponseEntity<>(ResDto.builder().success(true).build(), HttpStatus.OK);
	}

	public void notAuthAllUserRedirect(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {
		String userToken = jwtUtils.getAccessTokenInCookie(httpServletRequest);

		if(!(jwtUtils.verifyTokenInRedis(userToken) && jwtUtils.userAuthorization(userToken))){
			redisUtils.deleteData(jwtUtils.getEmail(userToken));

			Cookie cookie = new Cookie(cookieHeader, null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			httpServletResponse.addCookie(cookie);

			httpServletResponse.sendRedirect(UriComponentsBuilder.fromUriString(clientAddr)
				.build()
				.encode(StandardCharsets.UTF_8)
				.toString());
		}
	}
}
