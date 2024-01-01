/*
package com.sesac.auth_server_v1.auth.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sesac.auth_server_v1.auth.RedisData.AccessToken;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {
	Optional<AccessToken> findByToken(String token);
	Optional<AccessToken> findByEmail(String email);

	long countByEmail(String email);
	void deleteByEmail(String email);
}
*/
