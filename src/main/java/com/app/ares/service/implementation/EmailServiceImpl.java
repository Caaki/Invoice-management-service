package com.app.ares.service.implementation;

import com.app.ares.enumeration.VerificationType;
import com.app.ares.exception.ApiException;
import com.app.ares.service.EmailService;
import com.ctc.wstx.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType type) {

        try{

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("<Email>");
            message.setTo(email);
            message.setText(getEmailMessage(firstName, verificationUrl, type));
            message.setSubject(String.format("Ares - %s Verification email", StringUtils.capitalize(type.getType())));

            mailSender.send(message);

        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Sending email failed!");
        }


    }

    private String getEmailMessage(String firstName, String verificationUrl, VerificationType type) {

        switch (type){
            case PASSWORD -> {
                return  "Hello " + firstName +
                        "\n\nResetPassword request. Please click the link below too reset your password" +
                        "\n\n" + verificationUrl +
                        "\n\nAres Support Team.";
            }
            case ACCOUNT -> {
                return  "Hello " + firstName +
                        "\n\nYour new account has been created. Please click the link below too verify your account" +
                        "\n\n" + verificationUrl +
                        "\n\nAres Support Team.";
            }

            default -> throw new ApiException("Unable to send email. Email type unknown");
        }

    }
}
