package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.jwt.JwtDecoder;
import com.devtraining.systemdesign.jwt.JwtEncoder;
import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.devtraining.systemdesign.member.domain.MemberRepository;
import com.devtraining.systemdesign.member.domain.RefreshToken;
import com.devtraining.systemdesign.member.domain.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

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
    public TokenInfo login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.rawPassword();

        Member member = memberRepository
                .findWithAuthorityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        boolean matches = passwordEncoder.matches(password, member.getPassword());

        if (!matches) {
            throw new BadCredentialsException("Failed to login");
        }

        return createTokenInfo(member);
    }

    @Transactional
    public TokenInfo reissue(ReissueRequest reissueRequest) {
        String oldAccessToken = reissueRequest.accessToken();
        String oldRefreshToken = reissueRequest.refreshToken();

        verifyToRefresh(oldAccessToken, oldRefreshToken);

        String username = jwtDecoder.getUsername(oldRefreshToken);

        Member member = memberRepository
                .findWithAuthorityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return createTokenInfo(member);
    }

    private TokenInfo createTokenInfo(Member member) {
        String username = member.getUsername();

        Set<SimpleGrantedAuthority> authorities = member.getMemberAuthorities().stream()
                .map(MemberAuthority::getAuthority)
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        String accessToken = jwtEncoder.createAccessToken(username, authorities);
        String refreshToken = jwtEncoder.createRefreshToken(username, authorities);

        refreshTokenRepository.save(RefreshToken.builder()
                .key(username)
                .value(refreshToken)
                .ttl(jwtEncoder.getRefreshTokenTtl())
                .build());

        return new TokenInfo(accessToken, refreshToken);
    }

    private void verifyToRefresh(String accessToken, String refreshToken) {
        if (!jwtDecoder.isExpired(accessToken)) {
            throw new BadCredentialsException("AccessToken is not expired token");
        }

        String username = jwtDecoder.getUsername(refreshToken);

        RefreshToken refreshTokenInfo = refreshTokenRepository
                .findById(username)
                .orElseThrow(() -> new BadCredentialsException("RefreshToken not found"));

        String oldRefreshToken = refreshTokenInfo.getValue();

        if (!oldRefreshToken.equals(refreshToken)) {
            refreshTokenRepository.deleteById(refreshTokenInfo.getKey());
            throw new BadCredentialsException("Fail to refresh token");
        }
    }
}
