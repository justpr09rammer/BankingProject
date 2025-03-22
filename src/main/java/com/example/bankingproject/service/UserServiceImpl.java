package com.example.bankingproject.service;


import com.example.bankingproject.config.JwtTokenProvider;
import com.example.bankingproject.dto.*;
import com.example.bankingproject.entity.User;
import com.example.bankingproject.enums.UserStatus;
import com.example.bankingproject.repository.UserRepository;
import com.example.bankingproject.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;



    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // we re just trying to check if the user already exists by email or phone number
        if (userRepository.existsByEmail(userRequest.getEmail()) || userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
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
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(newUser);
        // here send email when a new user is created, so we need to know email details
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations your account has been created !\n" +
                        "Account name " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n Account number is " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmail(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }



    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You Have Logged In")
                .recipient(loginDto.getEmail())
                .messageBody("You have logged into your account. If this was not you, please contact the bank immediately.")
                .build();


        emailService.sendEmail(loginAlert);

        return BankResponse.builder()
                .responseCode("Login success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))

                .build();

    }


    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean doesAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean doesAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        if (!doesAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }


        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {


        boolean doesAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());

        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }



        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());


        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));


        userRepository.save(userToCredit);


        //Save transaction
        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean doesAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());


        if (userToDebit.getAccountBalance().compareTo(creditDebitRequest.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
        userRepository.save(userToDebit);
        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(creditDebitRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.SUCCESS_CODE)
                .responseMessage(AccountUtils.SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToDebit.getAccountBalance())
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                        .build())
                .build();

    }

    @Transactional
    public BankResponse transfer(TransferRequest transferRequest) {
        boolean doesDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if (!doesDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userSourceAccount = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
        User userDestinationAccount = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());

        if (userSourceAccount.getAccountBalance().compareTo(transferRequest.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


        userSourceAccount.setAccountBalance(userSourceAccount.getAccountBalance().subtract(transferRequest.getAmount()));
        userDestinationAccount.setAccountBalance(userDestinationAccount.getAccountBalance().add(transferRequest.getAmount()));

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(userSourceAccount.getEmail())
                .messageBody("The amount of " + transferRequest.getAmount() + " has been deducted from your account " + userSourceAccount.getAccountNumber() + ". Your current balance is " + userSourceAccount.getAccountBalance())
                .build();
        emailService.sendEmail(debitAlert);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(userDestinationAccount.getEmail())
                .messageBody("The amount of " + transferRequest.getAmount() + " has been credited to your account " + userDestinationAccount.getAccountNumber() + ". Your current balance is " + userDestinationAccount.getAccountBalance())
                .build();
        emailService.sendEmail(creditAlert);

        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(userDestinationAccount.getAccountNumber())
                .transactionType("CREDIT")
                .amount(transferRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);


        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE + "The amount of " + transferRequest.getAmount() + " has been deducted from your account " + userSourceAccount.getAccountNumber() + ". Your current balance is " + userSourceAccount.getAccountBalance() + "The amount of " + transferRequest.getAmount() + " has been credited to your account " + userDestinationAccount.getAccountNumber())
                .accountInfo(null)
                .build();
    }


    //balance Enquiry, name Enquiry, credit, debit, transfer
}