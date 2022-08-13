package com.filip.managementapp.security.util;

import com.filip.managementapp.security.jwt.JwtProps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    public static final String CLAIMS_AUTHORITIES_KEY = "authorities";
    private final SecretKey secretKey;
    @Getter
    private final JwtProps jwtProps;

    @Autowired
    public JwtTokenUtils(JwtProps jwtProps) {
        this.jwtProps = jwtProps;
        this.secretKey = Keys.hmacShaKeyFor(jwtProps.getSecretKey().getBytes());
    }

    public String generateJwtToken(Authentication auth) {
        var authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(auth.getName())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtProps.getTokenExpirationAfterDays())))
                .claim(CLAIMS_AUTHORITIES_KEY, authorities)
                .signWith(secretKey)
                .compact();
    }

    public String parseJwtTokenFromRequestHeader(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(jwtProps.getTokenPrefix())) {
            token = token.replace(jwtProps.getTokenPrefix(), "");
        }
        return token;
    }

    public Claims parseClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
