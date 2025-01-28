package com.example.bankingproject.controller;

import com.example.bankingproject.dto.BankResponse;
import com.example.bankingproject.dto.CreditDebitRequest;
import com.example.bankingproject.dto.EnquiryRequest;
import com.example.bankingproject.dto.UserRequest;
import com.example.bankingproject.repository.UserRepository;
import com.example.bankingproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @GetMapping("/enquiry/balance")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.balanceEnquiry(enquiryRequest);
    }
    @GetMapping("/enquiry/name")
    public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
        return userService.nameEnquiry(enquiryRequest);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.creditAccount(creditDebitRequest);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.debitAccount(creditDebitRequest);
    }


}
