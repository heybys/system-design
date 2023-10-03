package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    // private final UserCache userCache = new SpringCacheBasedUserCache(new ConcurrentMapCache("userCache"));
    private final UserCache userCache = new NullUserCache();

    private final UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();

    private final UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    private final GrantedAuthoritiesMapper authoritiesMapper = new SimpleAuthorityMapper();

    private final JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(
                JwtAuthenticationToken.class, authentication, () -> "Only JwtAuthenticationToken is supported");

        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("No credentials provided");
        }
        String accessToken = authentication.getCredentials().toString();

        boolean cacheWasUsed = true;
        UserDetails user;
        try {
            String username = jwtDecoder.getUsername(accessToken);
            user = this.userCache.getUserFromCache(username);

            if (user == null) {
                cacheWasUsed = false;
                user = jwtDecoder.getUserDetails(accessToken);
            }
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to get username from '" + accessToken + "'");
            throw new BadCredentialsException("Invalid accessToken");
        }

        Assert.notNull(user, "getUserDetails returned null - a violation of the interface contract");

        try {
            this.preAuthenticationChecks.check(user);
        } catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = jwtDecoder.getUserDetails(accessToken);
            this.preAuthenticationChecks.check(user);
        }
        this.postAuthenticationChecks.check(user);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        Object principalToReturn = user;

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    @Override
    public final void afterPropertiesSet() {
        Assert.notNull(this.userCache, "A user cache must be set");
    }

    private Authentication createSuccessAuthentication(
            Object principal, Authentication authentication, UserDetails user) {

        JwtAuthenticationToken result = JwtAuthenticationToken.authenticated(
                principal,
                authentication.getCredentials(),
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()));

        result.setDetails(authentication.getDetails());

        log.debug("Authenticated user");
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }

    private static class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                log.debug("Failed to authenticate since user account is locked");
                throw new LockedException("User account is locked");
            }
            if (!user.isEnabled()) {
                log.debug("Failed to authenticate since user account is disabled");
                throw new DisabledException("User is disabled");
            }
            if (!user.isAccountNonExpired()) {
                log.debug("Failed to authenticate since user account has expired");
                throw new AccountExpiredException("User account has expired");
            }
        }
    }

    private static class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                log.debug("Failed to authenticate since user account credentials have expired");
                throw new CredentialsExpiredException("User credentials have expired");
            }
        }
    }
}
