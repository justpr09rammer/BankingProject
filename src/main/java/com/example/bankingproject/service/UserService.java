package com.example.bankingproject.service;

import com.example.bankingproject.dto.BankResponse;
import com.example.bankingproject.dto.CreditDebitRequest;
import com.example.bankingproject.dto.EnquiryRequest;
import com.example.bankingproject.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse debitAccount(CreditDebitRequest creditDebitRequest);
}
