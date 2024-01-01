package com.sesac.auth_server_v1.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sesac.auth_server_v1.common.dto.ResDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class ExceptionController {
	@ExceptionHandler(ServiceLogicException.class)
	public ResponseEntity<ResDto> serviceLogicExceptionHandler(ServiceLogicException e){
		log.error(e.getMessage());
		log.error(e.getStackTrace());
		return exceptionToRes(e.getErrorStatus());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResDto> runtimeExceptionHandler(RuntimeException e) {
		log.error(e.getMessage());
		log.error(e.getStackTrace());
		return exceptionToRes(ErrorStatus.RUNTIME_EXCEPTION);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResDto> exceptionHandler(Exception e) {
		log.error(e.getMessage());
		log.error(e.getStackTrace());
		return exceptionToRes(ErrorStatus.INTERNAL_SERVER_ERROR);
	}
	private ResponseEntity<ResDto> exceptionToRes(ErrorStatus errorStatus){
		return ResponseEntity.status(errorStatus.getHttpStatus())
			.body(ResDto.builder()
				.success(false)
				.errorStatus(errorStatus)
				.build());
	}
}
