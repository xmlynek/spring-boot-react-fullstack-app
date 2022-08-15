package com.filip.managementapp.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class SecretKeyBean {
    @Value("${application.jwt.secret-key}")
    private String secretKeyString;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }
}
