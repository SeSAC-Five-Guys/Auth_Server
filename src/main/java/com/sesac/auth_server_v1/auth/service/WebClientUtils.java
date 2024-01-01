package com.sesac.auth_server_v1.auth.service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.sesac.auth_server_v1.config.WebClientConfig;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientUtils {
	private final WebClientConfig webClientConfig;

	public <T, V> Mono<T> getWithParam(String url, V request, Class<T> responseDtoClass) {
		return webClientConfig.webClient().method(HttpMethod.GET)
			.uri(url)
			.bodyValue(request)
			.retrieve()
			.bodyToMono(responseDtoClass);
	}

	public <T, V> Mono<T> post(String url, V requestDto, Class<T> responseDtoClass) {
		return webClientConfig.webClient().method(HttpMethod.POST)
			.uri(url)
			.bodyValue(requestDto)
			.retrieve()
			.bodyToMono(responseDtoClass);
	}

	public <T, V> Mono<T> put(String url, V requestDto, Class<T> responseDtoClass) {
		return webClientConfig.webClient().method(HttpMethod.PUT)
			.uri(url)
			.bodyValue(requestDto)
			.retrieve()
			.bodyToMono(responseDtoClass);
	}

	public <T> Mono<T> delete(String url, Class<T> responseDtoClass) {
		return webClientConfig.webClient().method(HttpMethod.DELETE)
			.uri(url)
			.retrieve()
			//.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
			//.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(InternalError::new))
			.bodyToMono(responseDtoClass);
	}
}
