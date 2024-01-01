/*
package com.sesac.auth_server_v1.auth.RedisData;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "access_Token")
public class AccessToken {
	@Id
	private String authInfoId;

	@Indexed
	private String email;

	@Indexed
	private String token;

	private String role;

	@TimeToLive
	private long ttl;

}
*/
