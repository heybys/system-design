package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

public record MemberInfo(
        @JsonProperty("username") String username,
        @JsonIgnore @JsonProperty("password") String rawPassword,
        @JsonProperty("authorities") List<String> authorityNames) {

    public static MemberInfo of(Member member) {
        return new MemberInfo(
                member.getUsername(),
                member.getPassword(),
                member.getMemberAuthorities().stream()
                        .map(MemberAuthority::getAuthority)
                        .map(Authority::getName)
                        .toList());
    }

    public Member toEncodedMember(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        return Member.builder().username(username).password(encodedPassword).build();
    }
}
