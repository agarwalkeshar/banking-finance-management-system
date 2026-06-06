package com.banking.app.service;

import com.banking.app.dto.request.LoginRequest;
import com.banking.app.dto.request.RegisterRequest;
import com.banking.app.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}