package com.devjobs.portal.service;

import com.devjobs.portal.entities.Otp;
import com.devjobs.portal.repositories.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpService {
    @Autowired
    OtpRepository otpRepository;

    public void createOtpEntry(Integer otp,String email){
        Otp newOtp = new Otp();
        newOtp.setOtp(otp);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setEmail(email);
        otpRepository.save(newOtp);
    }
}
