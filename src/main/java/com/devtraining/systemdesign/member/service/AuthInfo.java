package com.devtraining.systemdesign.member.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthInfo(
        @JsonProperty("accessToken") String accessToken, @JsonProperty("refreshToken") String refreshToken) {}
