package com.devtraining.systemdesign.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String rawPassword) {}
