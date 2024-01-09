package com.sesac.auth_server_v1.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.sesac.auth_server_v1.auth.service.JwtUtils;
import com.sesac.auth_server_v1.common.dto.ResDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/v1/authorization/all")
public class AllUserController {
	@Value("${variable.role.admin}")
	private String adminRole;

	private final JwtUtils jwtUtils;
	@GetMapping("/member")
	public ResponseEntity<ResDto> authorizationAllUser(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception{
		String userToken = jwtUtils.getAccessTokenInCookie(httpServletRequest);
		String memberRole = jwtUtils.getRole(userToken);
		Map<String, String> result = new HashMap<>();
		if(memberRole.equals(adminRole)){
			 result.put("role", "0");
		} else {
			result.put("role", "1");
		}
		return new ResponseEntity<>(ResDto.builder().success(true).data(result).build(), HttpStatus.OK);
	}
}
