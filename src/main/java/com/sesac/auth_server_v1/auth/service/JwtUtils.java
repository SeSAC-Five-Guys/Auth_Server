package com.sesac.auth_server_v1.auth.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
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
		try{
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(jwtSecretKey)
				.parseClaimsJws(token);

			return claims.getBody()
				.getExpiration()
				.after(new Date());
		} catch (Exception e){
			return false;
		}
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
			.get("role");
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
