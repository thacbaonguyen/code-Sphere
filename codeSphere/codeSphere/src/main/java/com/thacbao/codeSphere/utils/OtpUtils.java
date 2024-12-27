package com.thacbao.codeSphere.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtils {
    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
