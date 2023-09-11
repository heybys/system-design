package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.auth.service.AuthService;
import com.devtraining.systemdesign.auth.service.LoginRequest;
import com.devtraining.systemdesign.auth.service.SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    public static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {

        authService.signup(signupRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.login(loginRequest);

        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String accessToken = authService.login(authorization);

        return ResponseEntity.ok(accessToken);
    }
}
