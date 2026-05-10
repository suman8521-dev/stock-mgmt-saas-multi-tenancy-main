package com.saas.auth.service;

import com.saas.auth.requests.LoginRequest;
import com.saas.auth.responses.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(final LoginRequest request);
}
