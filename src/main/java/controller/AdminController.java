package com.banking.app.controller;

import com.banking.app.dto.response.AccountResponse;
import com.banking.app.dto.response.ApiResponse;
import com.banking.app.dto.response.TransactionResponse;
import com.banking.app.entity.*;
import com.banking.app.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // GET all users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Map<String, Object>> users = userRepository
                .findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("fullName", user.getFullName());
                    map.put("email", user.getEmail());
                    map.put("phone", user.getPhoneNumber());
                    map.put("role", user.getRole());
                    map.put("status", user.getStatus());
                    map.put("createdAt", user.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", users));
    }

    // GET single user + their accounts
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        List<AccountResponse> accounts = accountRepository
                .findByUser(user)
                .stream()
                .map(acc -> AccountResponse.builder()
                        .id(acc.getId())
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance())
                        .status(acc.getStatus().name())
                        .interestRate(acc.getInterestRate())
                        .createdAt(acc.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhoneNumber());
        result.put("role", user.getRole());
        result.put("status", user.getStatus());
        result.put("accounts", accounts);

        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", result));
    }

    // Freeze or activate a user
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.FROZEN);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(
                "User status updated to " + user.getStatus(), null));
    }

    // GET all transactions
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Map<String, Object>> transactions = transactionRepository
                .findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(txn -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", txn.getId());
                    map.put("referenceId", txn.getReferenceId());
                    map.put("type", txn.getType());
                    map.put("amount", txn.getAmount());
                    map.put("status", txn.getStatus());
                    map.put("description", txn.getDescription());
                    map.put("from", txn.getFromAccount() != null
                            ? txn.getFromAccount().getAccountNumber() : null);
                    map.put("to", txn.getToAccount() != null
                            ? txn.getToAccount().getAccountNumber() : null);
                    map.put("createdAt", txn.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(
                "Transactions fetched successfully", transactions));
    }

    // Dashboard stats
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {

        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();

        BigDecimal totalDeposits = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalAccounts", totalAccounts);
        stats.put("totalTransactions", totalTransactions);
        stats.put("totalDeposits", totalDeposits);

        return ResponseEntity.ok(
                ApiResponse.success("Dashboard stats", stats));
    }
}