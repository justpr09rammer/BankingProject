package com.example.bankingproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnquiryRequest {
    @Size(min = 10, max = 10, message = "Account number must be 10 digits")
    private String accountNumber;
}
