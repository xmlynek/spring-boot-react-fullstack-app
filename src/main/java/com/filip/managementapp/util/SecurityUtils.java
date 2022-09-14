package com.filip.managementapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

@Component
@Getter
public class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    private SecretKey secretKey;
    @Value("${application.jwt.secret-key}")
    private String secretKeyString;
    @Value("${application.jwt.token-expiration-after-days}")
    private int tokenExpirationAfterDays;
    @Value("${application.jwt.cookieName}")
    private String jwtCookieName;

    @PostConstruct
    private void postConstruct() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationAfterDays)))
                .signWith(secretKey)
                .compact();
    }

    public ResponseCookie generateJwtCookie(Authentication auth) {
        String generatedJwt = generateJwtToken(auth.getName());
        return ResponseCookie
                .from(jwtCookieName, generatedJwt)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(tokenExpirationAfterDays))
                .build();
    }

    public ResponseCookie deleteJwtCookie() {
        return ResponseCookie
                .from(jwtCookieName, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    /**
     * Method taken from https://www.bezkoder.com/spring-boot-login-example-mysql/
     */
    public boolean validateJwtToken(String token) {
        try {
            parseClaimsFromToken(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Other JWT exception: {}", e.getMessage());
        }
        return false;
    }

    public Cookie getJwtCookie(HttpServletRequest request) {
        return WebUtils.getCookie(request, jwtCookieName);
    }

    public Claims parseClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
