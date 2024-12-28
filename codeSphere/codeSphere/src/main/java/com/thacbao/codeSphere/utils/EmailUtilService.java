package com.thacbao.codeSphere.utils;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailUtilService {
    private final JavaMailSender javaMailSender;

    public void sentOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        mimeMessageHelper.setText("Here is your OTP code: <b>" + otp + "</b>", true);
        javaMailSender.send(mimeMessage);
    }

    public void sentResetPasswordEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Reset Password");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8083/api/v1/auth/set-password?email=%s&otp=%s" target="_blank">click link set password</a>
        </div>
        """.formatted(email, otp), true);
        javaMailSender.send(mimeMessage);
    }
}
