package com.example.bankingproject.service;

import com.example.bankingproject.dto.TransactionDto;
import com.example.bankingproject.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);

}
