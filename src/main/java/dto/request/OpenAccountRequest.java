package com.banking.app.dto.request;

import com.banking.app.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpenAccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}