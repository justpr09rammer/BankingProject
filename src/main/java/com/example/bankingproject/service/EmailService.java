package com.example.bankingproject.service;

import com.example.bankingproject.dto.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}