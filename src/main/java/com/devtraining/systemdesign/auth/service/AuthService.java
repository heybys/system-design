package com.devtraining.systemdesign.auth.service;

import com.devtraining.systemdesign.jwt.JwtProvider;
import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.AuthorityRepository;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.devtraining.systemdesign.member.domain.MemberAuthorityRepository;
import com.devtraining.systemdesign.member.domain.MemberRepository;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

    @PostConstruct
    public void init() {
        boolean isAdminPresent = memberRepository.findByUsername("admin").isPresent();
        if (isAdminPresent) {
            log.debug("Admin has already been created");
        } else {
            Member admin = Member.builder().username("admin").password("admin").build();
            admin.encodePassword(passwordEncoder);
            memberRepository.save(admin);

            grantAuthority(admin, AuthorityType.ADMIN);
        }
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        Member member = signupRequest.toMember();
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);

        grantAuthority(member, AuthorityType.USER);
    }

    private void grantAuthority(Member member, AuthorityType authorityType) {
        Authority defaultAuthority =
                Authority.builder().name(authorityType.name()).build();
        Authority authority =
                authorityRepository.findByName(authorityType.name()).orElse(defaultAuthority);
        authorityRepository.save(authority);

        MemberAuthority memberAuthority =
                MemberAuthority.builder().member(member).authority(authority).build();
        memberAuthorityRepository.save(memberAuthority);
    }

    @Transactional
    public String login(LoginRequest loginRequest) {
        return createAccessToken(loginRequest.username(), loginRequest.rawPassword());
    }

    @Transactional
    public String login(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(AUTHENTICATION_SCHEME_BASIC)) {
            throw new BadCredentialsException("Invalid authentication scheme");
        }
        byte[] base64Token = authorization.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = decode(base64Token);
        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        String username = token.substring(0, delim);
        String password = token.substring(delim + 1);

        return createAccessToken(username, password);
    }

    private String createAccessToken(String username, String password) {
        Member member = memberRepository
                .findWithAuthorityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        boolean matches = passwordEncoder.matches(password, member.getPassword());

        Set<Authority> authorities = member.getMemberAuthorities().stream()
                .map(MemberAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (matches) {
            return jwtProvider.createAccessToken(member.getUsername(), authorities);
        }

        throw new BadCredentialsException("Failed to login");
    }

    private byte[] decode(byte[] base64Token) {
        try {
            return Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException ex) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
    }
}
