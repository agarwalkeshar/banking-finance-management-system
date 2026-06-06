package com.banking.app.service.impl;

import com.banking.app.dto.request.TransactionRequest;
import com.banking.app.dto.response.TransactionResponse;
import com.banking.app.entity.*;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.banking.app.entity.User;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request, User user) {
        Account account = getAuthorizedAccount(
                request.getAccountNumber(), user);

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction txn = Transaction.builder()
                .toAccount(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Deposit")
                .build();

        return mapToResponse(transactionRepository.save(txn),
                account.getBalance());
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request, User user) {
        Account account = getAuthorizedAccount(
                request.getAccountNumber(), user);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds. Available: "
                    + account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction txn = Transaction.builder()
                .fromAccount(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Withdrawal")
                .build();

        return mapToResponse(transactionRepository.save(txn),
                account.getBalance());
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionRequest request, User user) {
        if (request.getToAccountNumber() == null) {
            throw new RuntimeException(
                    "Destination account number is required for transfer");
        }
        if (request.getAccountNumber().equals(request.getToAccountNumber())) {
            throw new RuntimeException(
                    "Cannot transfer to the same account");
        }

        Account fromAccount = getAuthorizedAccount(
                request.getAccountNumber(), user);
        Account toAccount = accountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException(
                        "Destination account not found: "
                                + request.getToAccountNumber()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds. Available: "
                    + fromAccount.getBalance());
        }

        // Atomic debit + credit — if anything fails, both roll back
        fromAccount.setBalance(
                fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(
                toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction txn = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Transfer")
                .build();

        return mapToResponse(transactionRepository.save(txn),
                fromAccount.getBalance());
    }

    @Override
    public Page<TransactionResponse> getTransactionHistory(
            String accountNumber, int page, int size, User user) {
        Account account = getAuthorizedAccount(accountNumber, user);
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository
                .findByAccount(account, pageable)
                .map(txn -> mapToResponse(txn, null));
    }

    private Account getAuthorizedAccount(String accountNumber, User user) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountNumber));
        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Unauthorized access to this account");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        return account;
    }

    private TransactionResponse mapToResponse(
            Transaction txn, BigDecimal balanceAfter) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .referenceId(txn.getReferenceId())
                .type(txn.getType().name())
                .amount(txn.getAmount())
                .status(txn.getStatus().name())
                .description(txn.getDescription())
                .fromAccountNumber(txn.getFromAccount() != null
                        ? txn.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(txn.getToAccount() != null
                        ? txn.getToAccount().getAccountNumber() : null)
                .balanceAfter(balanceAfter)
                .createdAt(txn.getCreatedAt())
                .build();
    }
}