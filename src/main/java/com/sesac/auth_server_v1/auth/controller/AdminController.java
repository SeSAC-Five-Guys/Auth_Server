package com.sesac.auth_server_v1.auth.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.util.UriComponentsBuilder;

import com.sesac.auth_server_v1.auth.service.JwtUtils;
import com.sesac.auth_server_v1.auth.service.RedisUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authorization/admin")
public class AdminController {
	@Value("${variable.jwt.cookieHeader}")
	private String cookieHeader;

	@Value("${variable.clientAddr")
	private String clientAddr;

	@Value("${variable.kibana}")
	private String kibana;

	@Value("${variable.grafana}")
	private String grafana;

	@Value("${variable.kafka-ui}")
	private String kafkaUI;

	@Value("${variable.argoCD}")
	private String argoCD;


	private final JwtUtils jwtUtils;
	private final RedisUtils redisUtils;
	@GetMapping("/five_guys/kibana")
	public void redirectKibana(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{

		notAuthAdminRedirecting(httpServletResponse, httpServletRequest);

		httpServletResponse.sendRedirect(UriComponentsBuilder.fromUriString(kibana)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toString());
	}
	@GetMapping("/five_guys/grafana")
	public void redirectGrafana(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{

		notAuthAdminRedirecting(httpServletResponse, httpServletRequest);

		httpServletResponse.sendRedirect(UriComponentsBuilder.fromUriString(grafana)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toString());
	}
	@GetMapping("/five_guys/kafkaui")
	public void redirectKafka_UI(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{

		notAuthAdminRedirecting(httpServletResponse, httpServletRequest);

		httpServletResponse.sendRedirect(UriComponentsBuilder.fromUriString(kafkaUI)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toString());
	}
	@GetMapping("/five_guys/argocd")
	public void redirectArgoCD(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{

		notAuthAdminRedirecting(httpServletResponse, httpServletRequest);

		httpServletResponse.sendRedirect(UriComponentsBuilder.fromUriString(argoCD)
			.build()
			.encode(StandardCharsets.UTF_8)
			.toString());
	}

	public void notAuthAdminRedirecting(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{
		String userToken = jwtUtils.getAccessTokenInCookie(httpServletRequest);

		if(!(jwtUtils.verifyTokenInRedis(userToken) && jwtUtils.adminAuthorization(userToken))){
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
