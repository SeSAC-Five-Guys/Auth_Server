package com.sesac.auth_server_v1.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.auth_server_v1.auth.dto.LoginDto;
import com.sesac.auth_server_v1.auth.dto.LoginResponseDto;

import com.sesac.auth_server_v1.auth.service.JwtUtils;
import com.sesac.auth_server_v1.auth.service.RedisUtils;
import com.sesac.auth_server_v1.auth.service.WebClientUtils;
import com.sesac.auth_server_v1.common.dto.ResDto;
import com.sesac.auth_server_v1.common.exception.ErrorStatus;
import com.sesac.auth_server_v1.common.exception.ServiceLogicException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

	@Value("${variable.memberReadAddr}" + "${variable.memberAPI}")
	private String memberLoginUrl;

	@Value("${variable.jwt.cookieHeader}")
	private String cookieHeader;

	@Value("${variable.jwt.access.period}")
	private long accessTokenPeriod;

	private final WebClientUtils webClientUtils;
	private final JwtUtils jwtUtils;
	private final RedisUtils redisUtils;
	@PostMapping("/member")
	public Mono<ResponseEntity<ResDto>> login(
		HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,
		@Valid @RequestBody LoginDto loginDto
	){
		return webClientUtils.getWithParam(memberLoginUrl, loginDto, ResDto.class)
			.flatMap(result -> {
				ObjectMapper objectMapper = new ObjectMapper();
				LoginResponseDto loginResponseDto = objectMapper.convertValue(result.getData(), LoginResponseDto.class);

				String email = loginResponseDto.getEmail();
				String nickname = loginResponseDto.getNickname();
				String memberRole = loginResponseDto.getMemberRole();

				String token = jwtUtils.generateAccessToken(email, nickname, memberRole);

				if(!(redisUtils.getData(email) == null)){
					redisUtils.deleteData(email);
				}
				Map<String, String> redisSaveAuth = new HashMap<>();
				redisSaveAuth.put("token", token);
				redisSaveAuth.put("memberRole", memberRole);

				redisUtils.setData(email, redisSaveAuth, accessTokenPeriod, TimeUnit.MILLISECONDS);

				HttpStatus httpStatus = result.getTmpSvcRes() != null ? HttpStatus.valueOf((Integer) result.getTmpSvcRes()) : HttpStatus.OK;
				result.setTmpSvcRes(null);

				Cookie accessTokenCookie = new Cookie(cookieHeader, token);
				accessTokenCookie.setHttpOnly(true);
				accessTokenCookie.setPath("/");
				httpServletResponse.addCookie(accessTokenCookie);

				if(result.getErrorStatus() == ErrorStatus.INTERNAL_SERVER_ERROR){
					return Mono.error(new ServiceLogicException(ErrorStatus.INTERNAL_SERVER_ERROR));
				}

				return Mono.just(new ResponseEntity<>(result, httpStatus));
			});
	}

	@DeleteMapping("/member")
	public ResponseEntity<ResDto> logout(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
		redisUtils.deleteData(
			jwtUtils.getEmail(
				jwtUtils.getAccessTokenInCookie(httpServletRequest)
			));

		Cookie cookie = new Cookie(cookieHeader, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		httpServletResponse.addCookie(cookie);
		return new ResponseEntity<>(ResDto.builder().success(true).build(), HttpStatus.OK);
	}
}