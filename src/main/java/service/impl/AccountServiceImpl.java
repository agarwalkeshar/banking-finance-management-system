package com.banking.app.service.impl;

import com.banking.app.dto.request.OpenAccountRequest;
import com.banking.app.dto.response.AccountResponse;
import com.banking.app.entity.*;
import com.banking.app.repository.AccountRepository;
import com.banking.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountResponse openAccount(OpenAccountRequest request, User user) {
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .interestRate(new BigDecimal("3.50"))
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    @Override
    public List<AccountResponse> getMyAccounts(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber, User user) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountNumber));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this account");
        }
        return mapToResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse closeAccount(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this account");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException(
                    "Cannot close account with non-zero balance");
        }

        account.setStatus(AccountStatus.CLOSED);
        return mapToResponse(accountRepository.save(account));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC" + (1000000000L + new Random()
                    .nextInt(900000000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .interestRate(account.getInterestRate())
                .createdAt(account.getCreatedAt())
                .build();
    }
}