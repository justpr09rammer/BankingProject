package com.example.bankingproject.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class demoController {
    @RequestMapping("get")
    public String getName(){
        return "Hello World";
    }
}
