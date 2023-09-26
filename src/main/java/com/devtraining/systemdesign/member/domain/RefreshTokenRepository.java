package com.devtraining.systemdesign.member.domain;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends KeyValueRepository<RefreshToken, String> {}
