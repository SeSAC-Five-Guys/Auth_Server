package com.sesac.auth_server_v1.common.exception;

import lombok.Getter;

@Getter
public class ServiceLogicException extends RuntimeException{
	private final ErrorStatus errorStatus;
	public ServiceLogicException(ErrorStatus errorStatus){
		super(errorStatus.getDescription());
		this.errorStatus = errorStatus;
	}
}
