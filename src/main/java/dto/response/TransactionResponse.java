package com.banking.app.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Long id;
    private String referenceId;
    private String type;
    private BigDecimal amount;
    private String status;
    private String description;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}