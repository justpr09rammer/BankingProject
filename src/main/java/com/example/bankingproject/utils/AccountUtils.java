package com.example.bankingproject.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account CREATED";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account creation successful";

    //it should be smth like current year + sixDigit
    // 2025 ... ...
    public static String generateAccountNumber() {

        Year currentYear = Year.now();

        Random random = new Random();
        int randomSixDigit = 100000 + random.nextInt(900000); // Random number between 100000 and 999999

        String year = String.valueOf(currentYear.getValue());
        String randomNumber = String.valueOf(random.nextInt(randomSixDigit));

        StringBuilder accountNumber = new StringBuilder();


        return accountNumber.append(year).append(randomNumber).toString();

    }

}
