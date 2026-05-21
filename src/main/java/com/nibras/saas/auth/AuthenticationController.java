package com.nibras.saas.auth;

import com.nibras.saas.auth.requests.LoginRequest;
import com.nibras.saas.auth.responses.LoginResponse;
import com.nibras.saas.auth.service.AuthenticationService;
import com.nibras.saas.dto.request.RegisterTenantRequest;
import com.nibras.saas.service.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request) {
        final LoginResponse response = this.authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody final RegisterTenantRequest request) {
        this.tenantService.registerTenant(request);
        return ResponseEntity.ok().build();
    }

}
