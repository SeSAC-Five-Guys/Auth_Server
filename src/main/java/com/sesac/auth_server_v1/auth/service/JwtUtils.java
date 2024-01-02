package com.sesac.auth_server_v1.auth.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sesac.auth_server_v1.common.exception.ErrorStatus;
import com.sesac.auth_server_v1.common.exception.ServiceLogicException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtils {
	@Value("${variable.jwt.secretKey}")
	private String jwtSecretKey;

	@Value("${variable.jwt.access.period}")
	private long accessTokenPeriod;

	@Value("${variable.jwt.access.dateFormat}")
	private String dateFormat;

	@Value("${variable.jwt.access.tokenHeader}")
	private String tokenHeader;

	@Value("${variable.jwt.cookieHeader}")
	private String cookieHeader;

	@Value("${variable.role.admin}")
	private String roleAdmin;

	@Value("${variable.role.user}")
	private String roleUser;

	private final RedisUtils redisUtils;
	@PostConstruct
	protected void init() {
		jwtSecretKey = Base64.getEncoder().encodeToString(jwtSecretKey.getBytes());
	}

	public String generateAccessToken(String email, String nickname, String memberRole) {
		Claims claims = Jwts.claims().setSubject(tokenHeader);
		claims.put("email", email);
		claims.put("nickname", nickname);
		claims.put("memberRole", memberRole);

		Date now = new Date();

		return Jwts.builder()
					.setClaims(claims)
					.setIssuedAt(now)
					.setExpiration(new Date(now.getTime() + accessTokenPeriod))
					.signWith(SignatureAlgorithm.HS256, jwtSecretKey)
					.compact();
	}

	public boolean verifyToken(String token){
		if(!(Jwts.parser()
			.setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody()
			.getExpiration().after(new Date()))){
			throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
		}
		return true;
	}

	public boolean verifyTokenInRedis(String userToken) {
		String memberRole = getRole(userToken);
		String email = getEmail(userToken);

		if (!userToken.equals(redisUtils.getData(email).get("token")) ||
			!memberRole.equals(redisUtils.getData(email).get("memberRole"))) {
			throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
		}
		return true;
	}

	public boolean adminAuthorization(String userToken){
		String userRole = getRole(userToken);

		if (!userRole.equals(roleAdmin)){
			throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
		}
		return true;
	}

	public boolean userAuthorization(String userToken){
		String userRole = getRole(userToken);

		if(!(userRole.equals(roleAdmin) || userRole.equals(roleUser))){
			throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
		}
		return true;
	}
	public String getAccessTokenInCookie(HttpServletRequest httpServletRequest){
		Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieHeader)) {
					return cookie.getValue();
				}
			}
		} throw new ServiceLogicException(ErrorStatus.ACCESS_TOKEN_ERROR);
	}
	public String getEmail(String token) {
		return (String)Jwts.parser()
			.setSigningKey(jwtSecretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("email");
	}

	public String getRole(String token) {
		return (String)Jwts.parser()
			.setSigningKey(jwtSecretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("memberRole");
	}

	public String getNickname(String token) {
		return (String)Jwts.parser()
			.setSigningKey(jwtSecretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("name");
	}

	public Date getExpiration(String token) {
		return (Date)Jwts.parser()
			.setSigningKey(jwtSecretKey)
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();
	}

	public String dateToString(String token) {
		DateFormat expirationFormat = new SimpleDateFormat(dateFormat);
		Date tokenExpirationDate = getExpiration(token);
		return expirationFormat.format(tokenExpirationDate);
	}
}
