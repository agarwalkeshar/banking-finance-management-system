package com.banking.app.controller;

import com.banking.app.dto.request.OpenAccountRequest;
import com.banking.app.dto.response.AccountResponse;
import com.banking.app.dto.response.ApiResponse;
import com.banking.app.entity.User;
import com.banking.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    public ResponseEntity<ApiResponse<AccountResponse>> openAccount(
            @Valid @RequestBody OpenAccountRequest request,
            @AuthenticationPrincipal User user) {
        AccountResponse response = accountService.openAccount(request, user);
        return ResponseEntity.ok(
                ApiResponse.success("Account opened successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts(
            @AuthenticationPrincipal User user) {
        List<AccountResponse> accounts = accountService.getMyAccounts(user);
        return ResponseEntity.ok(
                ApiResponse.success("Accounts fetched successfully", accounts));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal User user) {
        AccountResponse response = accountService
                .getAccountByNumber(accountNumber, user);
        return ResponseEntity.ok(
                ApiResponse.success("Account fetched successfully", response));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<AccountResponse>> closeAccount(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        AccountResponse response = accountService.closeAccount(id, user);
        return ResponseEntity.ok(
                ApiResponse.success("Account closed successfully", response));
    }
}