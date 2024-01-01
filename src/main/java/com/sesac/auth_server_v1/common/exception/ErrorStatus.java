package com.sesac.auth_server_v1.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorStatus {
	INTERNAL_SERVER_ERROR(500, "COMMON-001", "서버에서 처리할 수 없는 경우."),
	RUNTIME_EXCEPTION(400, "COMMON-002", "잘못된 요청."),

	ACCESS_TOKEN_ERROR(401, "Auth-001", "엑세스 토큰 오류."),

	NOT_EXIST_EMAIL(400, "Member-00", "이메일이 존재하지 않는 경우."),
	NOT_EXIST_PW(400, "Member-00", "이메일은 존재하지만 비밀번호가 존재하지 않는 경우.");

	private int status;
	private String code;
	private String description;

	// ErrorCode에 지정된 status 필드에 상응하는 HttpStatus 를 반환하는 메서드
	public HttpStatus getHttpStatus(){
		return HttpStatus.valueOf(status);
	}
}
