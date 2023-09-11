package com.devtraining.systemdesign.auth.service;

import com.devtraining.systemdesign.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SignupRequest(@JsonProperty("username") String username, @JsonProperty("password") String rawPassword) {

    public Member toMember() {
        return Member.builder().username(username).password(rawPassword).build();
    }
}
