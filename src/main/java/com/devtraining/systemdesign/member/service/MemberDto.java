package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberDto(
        @JsonProperty("username") String username,
        @JsonIgnore @JsonProperty("password") String rawPassword,
        @JsonProperty("authorities") List<AuthorityType> authorityTypes) {

    public static MemberDto of(Member member) {
        return new MemberDto(
                member.getUsername(),
                member.getPassword(),
                member.getMemberAuthorities().stream()
                        .map(MemberAuthority::getAuthority)
                        .map(Authority::getName)
                        .map(AuthorityType::valueOf)
                        .toList());
    }

    public Member toEntity() {
        return Member.builder().username(username).password(rawPassword).build();
    }
}
