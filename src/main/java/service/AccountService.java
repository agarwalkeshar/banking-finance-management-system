package com.banking.app.service;

import com.banking.app.dto.request.OpenAccountRequest;
import com.banking.app.dto.response.AccountResponse;
import com.banking.app.entity.User;

import java.util.List;

public interface AccountService {
    AccountResponse openAccount(OpenAccountRequest request, User user);
    List<AccountResponse> getMyAccounts(User user);
    AccountResponse getAccountByNumber(String accountNumber, User user);
    AccountResponse closeAccount(Long accountId, User user);
}