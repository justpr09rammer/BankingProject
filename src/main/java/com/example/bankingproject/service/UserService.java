package com.example.bankingproject.service;

import com.example.bankingproject.dto.BankResponse;
import com.example.bankingproject.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userrequest);
}
