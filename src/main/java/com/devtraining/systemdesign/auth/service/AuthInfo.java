package com.devtraining.systemdesign.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthInfo(
        @JsonProperty("accessToken") String accessToken, @JsonProperty("refreshToken") String refreshToken) {}
