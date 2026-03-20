package com.iso.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordChangeEmail(String toEmail, String Username) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Changed - Security Notification");

        message.setText(
                "Hello " + Username + ",\n\n" +
                "Your account password has been successfully changed.\n\n" +
                "If you did NOT perform this action, please contact support immediately.\n\n" +
                "Regards,\n" +
                "System Security Team"
        );

        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Your Password");

        message.setText(
                "Hello " + fullName + ",\n\n" +
                "Click the link below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you did not request this, ignore this email.\n\n" +
                "Regards,\nSystem Security Team"
        );

        mailSender.send(message);
    }

}

