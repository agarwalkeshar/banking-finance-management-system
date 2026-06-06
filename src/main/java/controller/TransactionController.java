package com.banking.app.controller;

import com.banking.app.dto.request.TransactionRequest;
import com.banking.app.dto.response.ApiResponse;
import com.banking.app.dto.response.TransactionResponse;
import com.banking.app.entity.User;
import com.banking.app.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user) {
        TransactionResponse response = transactionService.deposit(request, user);
        return ResponseEntity.ok(
                ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user) {
        TransactionResponse response = transactionService.withdraw(request, user);
        return ResponseEntity.ok(
                ApiResponse.success("Withdrawal successful", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user) {
        TransactionResponse response = transactionService.transfer(request, user);
        return ResponseEntity.ok(
                ApiResponse.success("Transfer successful", response));
    }

    @GetMapping("/{accountNumber}/history")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Page<TransactionResponse> history = transactionService
                .getTransactionHistory(accountNumber, page, size, user);
        return ResponseEntity.ok(
                ApiResponse.success("Transaction history fetched", history));
    }
}