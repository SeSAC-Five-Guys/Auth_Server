package com.sesac.auth_server_v1.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
	private String createdAt;
	private String updatedAt;
	private String deletedAt;

	private String email;
	private String phoneNumber;
	private String nickname;
	private String memberRole;
}
