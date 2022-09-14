package com.filip.managementapp.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@Import({
        SecurityUtils.class
})
@TestPropertySource("classpath:application-test.properties")
class SecurityUtilsTest {

    @Autowired
    private SecurityUtils securityUtils;

    private final String email = "email123@gmail.com";


    @Test
    void shouldLoadContextAndCreateSecretKey() {
        assertThat(securityUtils)
                .isNotNull()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    void shouldGenerateJwtToken() {
        String jwtToken = securityUtils.generateJwtToken(email);

        assertThat(jwtToken)
                .isNotNull()
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(64);
    }

    @Test
    void shouldGenerateJwtCookie() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, "kebab123");

        ResponseCookie responseCookie = securityUtils.generateJwtCookie(authentication);

        assertThat(responseCookie).isNotNull();
        assertThat(responseCookie.getName()).isEqualTo(securityUtils.getJwtCookieName());
        assertThat(responseCookie.getValue()).isNotNull().isNotEmpty();
        assertThat(responseCookie.getPath()).isEqualTo("/");
        assertThat(responseCookie.isHttpOnly()).isTrue();
        assertThat(responseCookie.getMaxAge()).isEqualTo(Duration.of(securityUtils.getTokenExpirationAfterDays(), ChronoUnit.DAYS));
    }

    @Test
    void generateJwtCookieShouldThrowNullPointerException() {
        assertThatThrownBy(() -> securityUtils.generateJwtCookie(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldGenerateDeleteJwtCookie() {
        ResponseCookie responseCookie = securityUtils.deleteJwtCookie();
        System.out.println(responseCookie);

        assertThat(responseCookie).isNotNull();
        assertThat(responseCookie.getName()).isEqualTo(securityUtils.getJwtCookieName());
        assertThat(responseCookie.getPath()).isEqualTo("/");
        assertThat(responseCookie.getMaxAge()).isZero();
        assertThat(responseCookie.isHttpOnly()).isTrue();
    }

    @Test
    void shouldGetJwtCookieFromRequest() {
        Cookie requestCookie = new Cookie(securityUtils.getJwtCookieName(), securityUtils.generateJwtToken(email));
        requestCookie.setPath("/");
        requestCookie.setHttpOnly(true);
        requestCookie.setMaxAge(securityUtils.getTokenExpirationAfterDays());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(requestCookie);

        Cookie cookie = securityUtils.getJwtCookie(request);

        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(requestCookie.getValue());
        assertThat(cookie.getName()).isEqualTo(securityUtils.getJwtCookieName());
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(requestCookie.getMaxAge());
        assertThat(cookie.getPath()).isEqualTo(requestCookie.getPath());
    }

    @Test
    void getJwtCookieFromRequestWillThrowException() {
        assertThatThrownBy(() -> securityUtils.getJwtCookie(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request must not be null");
    }

    @Test
    void shouldParseClaimsFromToken() {
        String jwtToken = securityUtils.generateJwtToken(email);

        Claims claims = securityUtils.parseClaimsFromToken(jwtToken);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isNotNull().isNotEmpty().isEqualTo(email);
    }

    @Test
    void parseClaimsFromTokenShouldThrowException() {
        assertThatThrownBy(() -> securityUtils.parseClaimsFromToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT String argument cannot be null or empty.");
    }

    @Test
    void validateJwtTokenShouldReturnTrue() {
        String jwtToken = securityUtils.generateJwtToken(email);
        System.out.println(jwtToken);
        boolean response = securityUtils.validateJwtToken(jwtToken);

        assertTrue(response);
    }

    @Test
    void validateJwtTokenShouldReturnFalse(CapturedOutput output) {
        boolean response = securityUtils.validateJwtToken("jwtToken");
        assertFalse(response);
        assertThat(output.toString()).contains("Invalid JWT token: JWT strings must contain exactly 2 period characters. Found: 0");
    }
}