package com.devtraining.systemdesign.member.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String rawPassword) {}
