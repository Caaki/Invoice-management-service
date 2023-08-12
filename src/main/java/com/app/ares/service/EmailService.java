package com.app.ares.service;

import com.app.ares.enumeration.VerificationType;

public interface EmailService {

    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType type);

}
