package com.example.demo.SERVICES;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AbstractEmailService {
    public void sendEmailWithAttachment(String destination, String votreFactureEstPrête, String s, byte[] pdf, String s1) {
        System.out.println("Email Sent at: "+ LocalDateTime.now());

    }
}
