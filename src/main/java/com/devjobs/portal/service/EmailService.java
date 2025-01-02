package com.devjobs.portal.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {

//    this bean will work after we set all the properties in application.yml
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to,String subject,String body){
        try{
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
        }
        catch(Exception e){
            log.error("Exception while sending Email",e);
        }
    }
}
