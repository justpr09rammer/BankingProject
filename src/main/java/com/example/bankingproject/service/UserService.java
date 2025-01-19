package com.example.bankingproject.service;

import com.example.bankingproject.dto.AccountInfo;
import com.example.bankingproject.dto.BankResponse;
import com.example.bankingproject.dto.UserRequest;
import com.example.bankingproject.entity.User;
import com.example.bankingproject.enums.UserStatus;
import com.example.bankingproject.repository.UserRepository;
import com.example.bankingproject.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService implements implUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // we re just tryna check if the user already exists by email or phone number
        if (userRepository.existsByEmail(userRequest.getEmail()) || userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }
}
