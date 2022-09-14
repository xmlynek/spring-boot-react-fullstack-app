package com.filip.managementapp.security.filter;

import com.filip.managementapp.service.MyUserDetailsService;
import com.filip.managementapp.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenVerifierFilter extends OncePerRequestFilter {

    private static final Logger customLogger = LoggerFactory.getLogger(JwtTokenVerifierFilter.class);

    private final SecurityUtils securityUtils;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie jwtCookie = securityUtils.getJwtCookie(request);
            if(jwtCookie != null && securityUtils.validateJwtToken(jwtCookie.getValue())) {
                String jwtToken = jwtCookie.getValue();
                Claims body = securityUtils.parseClaimsFromToken(jwtToken);

                UserDetails userDetails = myUserDetailsService.loadUserByUsername(body.getSubject());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        body.getSubject(),
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            customLogger.error("JwtTokenVerifierFilter Exception {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
