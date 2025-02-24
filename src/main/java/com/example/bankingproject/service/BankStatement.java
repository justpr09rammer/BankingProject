package com.example.bankingproject.service;

import com.example.bankingproject.dto.EmailDetails;
import com.example.bankingproject.entity.Transaction;
import com.example.bankingproject.entity.User;
import com.example.bankingproject.repository.TransactionRepository;
import com.example.bankingproject.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itextpdf.text.Annotation.FILE;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;


    private EmailService emailService;



    private static final String FILE = "/Users/nihadgojayev/Desktop/Mystatement.pdf";



    public List<Transaction> generateStatement(String accountNumber,String startDate,String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate end = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + user.getOtherName();



        Rectangle stateSize = new Rectangle(PageSize.A4);
        Document document = new Document(stateSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("ltc bankimiz"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.YELLOW);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("baku, khatai"));
        bankName.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);



        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("Statement of account"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date " + endDate));
        stopDate.setBorder(0);

        PdfPCell name = new PdfPCell(new Phrase("Customer Name " + customerName));
        name.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        PdfPCell address = new PdfPCell(new Phrase("Address " + user.getAddress()));
        address.setBorder(0);



        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Date "));
        date.setBackgroundColor(BaseColor.YELLOW);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.YELLOW);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.YELLOW);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("Status"));
        status.setBackgroundColor(BaseColor.YELLOW);
        status.setBorder(0);


        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {

            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getStatus()));

        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()

                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested ...")
                .attachment(FILE)


                .build();
        emailService.sendEmail(emailDetails);



        return transactionList;
    }


}
