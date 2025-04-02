package com.thacbao.codeSphere.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOsConfig {
    @Value("${PAYOS.PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS.PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS.PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
