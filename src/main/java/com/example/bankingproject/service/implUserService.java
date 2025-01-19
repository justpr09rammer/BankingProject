package com.example.bankingproject.service;

import com.example.bankingproject.dto.BankResponse;
import com.example.bankingproject.dto.UserRequest;
import com.example.bankingproject.entity.User;

public interface implUserService {
    BankResponse createAccount(UserRequest userrequest);
}
