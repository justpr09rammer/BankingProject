package com.example.bankingproject;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title="The Java Academy Bank App",
                description = "Backend Rest APIs for X Bank",
//                version summary = "v1.0", //?
                contact = @Contact(
                        name ="Ilaha Jamalli",
                        email = "camalliilahe@gmail.com",
                        url = "https://github.com/justpr09rammer/BankingProject.git"
                ),
                license = @License(
                        name ="LTC bank",
                        url = "https://github.com/justpr09rammer/BankingProject.git"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "",
                url ="https://github.com/justpr09rammer/BankingProject.git"
        )
)
public class BankingProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingProjectApplication.class, args);
    }

}
