package com.filip.managementapp.security.filter;

import com.filip.managementapp.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtTokenVerifierFilter extends OncePerRequestFilter {
    private final SecurityUtils securityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie jwtCookie = securityUtils.getJwtCookie(request);
            if(jwtCookie != null) {
                String jwtToken = jwtCookie.getValue();
                Claims body = securityUtils.parseClaimsFromToken(jwtToken);

                Set<String> authorities = new HashSet<>(body.get(SecurityUtils.CLAIMS_AUTHORITIES_KEY, List.class));
                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        body.getSubject(),
                        null,
                        simpleGrantedAuthorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            throw new IllegalStateException("Invalid jwt token");
        }
        filterChain.doFilter(request, response);
    }
}
