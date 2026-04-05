package com.example.traceline_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCredentials(String toEmail, String username, String password, String role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("TraceLine Account Created");
        message.setText(String.format(
                "Hello,\n\nAn account has been created for you on TraceLine.\n\n" +
                        "Role: %s\nUsername: %s\nPassword: %s\n\n" +
                        "Please log in and change your password.\n\nTraceLine Team",
                role, username, password
        ));
        mailSender.send(message);
    }

    public void sendSuspensionNotice(String toEmail, String operatorName, String reason, int days) {
        String subject = "Account Suspension Notice - TraceLine";
        String body = String.format(
                "Dear %s,\n\nYour operator account has been suspended for %d day(s).\nReason: %s\n\nYou will not be able to log in until the suspension period ends.\n\nTraceLine Admin Team",
                operatorName, days, reason
        );
        sendSimpleEmail(toEmail, subject, body);
    }

    public void sendDeletionNotice(String toEmail, String operatorName) {
        String subject = "Account Deletion Notice - TraceLine";
        String body = String.format(
                "Dear %s,\n\nYour operator account has been permanently deleted.\nYou no longer have access to TraceLine.\n\nIf you believe this is an error, please contact your administrator.\n\nTraceLine Admin Team",
                operatorName
        );
        sendSimpleEmail(toEmail, subject, body);
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        if (to == null || to.isEmpty()) {
            System.out.println("No email address provided, skipping notification.");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            logToConsole(to, subject, text);
        }
    }

    private void logToConsole(String to, String subject, String body) {
        System.out.println("\n========== EMAIL NOTIFICATION (CONSOLE FALLBACK) ==========");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("==========================================================\n");
    }
}