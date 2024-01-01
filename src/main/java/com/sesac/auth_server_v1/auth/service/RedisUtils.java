package com.sesac.auth_server_v1.auth.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisUtils {
	private final RedisTemplate<String, Object> redisTemplate;
	public void setData(String key, Map<String, String> value, Long expiredTime, TimeUnit timeUnit){
		redisTemplate.opsForHash().putAll(key, value);
		redisTemplate.expire(key, expiredTime, timeUnit);
	}

	public Map<Object, Object> getData(String key){
		return redisTemplate.opsForHash().entries(key);
	}
	public void deleteData(String key){
		redisTemplate.delete(key);
	}
}
