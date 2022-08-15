package com.filip.managementapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
@RequiredArgsConstructor
public class SecurityUtils {
    public static final String CLAIMS_AUTHORITIES_KEY = "authorities";
    private final SecretKey secretKey;
    @Value("${application.jwt.token-expiration-after-days}")
    private int tokenExpirationAfterDays;
    @Value("${application.jwt.cookieName}")
    private String jwtCookieName;

    public String generateJwtToken(Authentication auth) {
        Set<String> authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(auth.getName())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationAfterDays)))
                .claim(CLAIMS_AUTHORITIES_KEY, authorities)
                .signWith(secretKey)
                .compact();
    }

    public ResponseCookie generateJwtCookie(Authentication auth) {
        String generatedJwt = generateJwtToken(auth);
        return ResponseCookie
                .from(jwtCookieName, generatedJwt)
                .path("/api")
                .httpOnly(true)
                .build();
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
