package com.sesac.auth_server_v1.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sesac.auth_server_v1.common.exception.ErrorStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResDto<T>{
	private boolean success;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T tmpSvcRes;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ErrorStatus errorStatus;
}
