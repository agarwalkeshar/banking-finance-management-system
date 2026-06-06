package com.banking.app.service;

import com.banking.app.dto.request.TransactionRequest;
import com.banking.app.dto.response.TransactionResponse;
import com.banking.app.entity.User;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionResponse deposit(TransactionRequest request, User user);
    TransactionResponse withdraw(TransactionRequest request, User user);
    TransactionResponse transfer(TransactionRequest request, User user);
    Page<TransactionResponse> getTransactionHistory(
            String accountNumber, int page, int size, User user);
}