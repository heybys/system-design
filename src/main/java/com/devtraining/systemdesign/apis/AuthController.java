package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.member.service.AuthInfo;
import com.devtraining.systemdesign.member.service.AuthService;
import com.devtraining.systemdesign.member.service.LoginRequest;
import com.devtraining.systemdesign.member.service.SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {

        authService.signup(signupRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthInfo> login(@RequestBody LoginRequest loginRequest) {
        AuthInfo authInfo = authService.login(loginRequest);
        return ResponseEntity.ok(authInfo);
    }
}
