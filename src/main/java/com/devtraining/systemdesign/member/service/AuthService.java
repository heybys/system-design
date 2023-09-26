package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.jwt.JwtProvider;
import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.devtraining.systemdesign.member.domain.MemberRepository;
import com.devtraining.systemdesign.member.domain.RefreshToken;
import com.devtraining.systemdesign.member.domain.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin";

    private final Duration accessTokenTtl = Duration.ofSeconds(5);
    private final Duration refreshTokenTtl = Duration.ofSeconds(10);

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void init() {
        boolean isAdminPresent = memberRepository.findByUsername(ADMIN_USERNAME).isPresent();
        if (isAdminPresent) {
            log.debug("Admin has already been created");
        } else {
            MemberDto memberDto = MemberDto.builder()
                    .username(ADMIN_USERNAME)
                    .rawPassword(ADMIN_PASSWORD)
                    .authorityTypes(List.of(AuthorityType.ADMIN))
                    .build();

            Long memberId = memberService.createMember(memberDto);
            log.debug("Created admin. memberId: {}", memberId);
        }
    }

    @Transactional
    public void signup(SignupRequest signupRequest) {
        MemberDto memberDto = MemberDto.builder()
                .username(signupRequest.username())
                .rawPassword(signupRequest.rawPassword())
                .authorityTypes(List.of(AuthorityType.USER))
                .build();

        Long memberId = memberService.createMember(memberDto);
        log.debug("Created member. memberId: {}", memberId);
    }

    @Transactional
    public AuthInfo login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.rawPassword();

        Member member = memberRepository
                .findWithAuthorityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        boolean matches = passwordEncoder.matches(password, member.getPassword());

        if (!matches) {
            throw new BadCredentialsException("Failed to login");
        }

        Set<Authority> authorities = member.getMemberAuthorities().stream()
                .map(MemberAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtProvider.createAccessToken(username, authorities, accessTokenTtl);
        String refreshToken = jwtProvider.createRefreshToken(username, authorities, refreshTokenTtl);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .key(username)
                .value(refreshToken)
                .ttl(refreshTokenTtl)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthInfo(accessToken, refreshToken);
    }
}
