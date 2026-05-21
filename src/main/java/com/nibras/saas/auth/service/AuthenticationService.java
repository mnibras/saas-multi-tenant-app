package com.nibras.saas.auth.service;

import com.nibras.saas.auth.requests.LoginRequest;
import com.nibras.saas.auth.responses.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(final LoginRequest request);
}
