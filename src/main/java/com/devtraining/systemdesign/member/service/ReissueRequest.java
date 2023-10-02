package com.devtraining.systemdesign.member.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReissueRequest(
        @JsonProperty("accessToken") String accessToken, @JsonProperty("refreshToken") String refreshToken) {}
