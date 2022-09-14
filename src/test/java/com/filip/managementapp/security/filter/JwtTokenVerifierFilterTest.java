package com.filip.managementapp.security.filter;

import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.security.MyUserDetails;
import com.filip.managementapp.service.MyUserDetailsService;
import com.filip.managementapp.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class, OutputCaptureExtension.class})
@Import({
        SecurityUtils.class
})
@TestPropertySource("classpath:application-test.properties")
class JwtTokenVerifierFilterTest {

    @Autowired
    private SecurityUtils securityUtils;

    @Mock
    private MyUserDetailsService myUserDetailsService;

    @InjectMocks
    private JwtTokenVerifierFilter jwtTokenVerifierFilter;


    @BeforeEach
    void setUp() {
        jwtTokenVerifierFilter = new JwtTokenVerifierFilter(securityUtils, myUserDetailsService);
    }

    @Test
    void jwtTokenVerifierFilterShouldAuthenticate() throws Exception {
        // given
        String email = "email123@gmail.com";
        User user = new User(1L,
                "Username",
                "Lastname",
                "email123@gmail.com",
                "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
                Gender.OTHER,
                LocalDate.of(2000, 3, 3),
                true,
                Set.of(new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>()))
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(securityUtils.getJwtCookieName(), securityUtils.generateJwtToken(email)));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        given(myUserDetailsService.loadUserByUsername(email)).willReturn(new MyUserDetails(user));

        // when
        jwtTokenVerifierFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth)
                .isNotNull()
                .hasFieldOrPropertyWithValue("Principal", email)
                .hasFieldOrPropertyWithValue("Authenticated", true);
        assertThat(auth.getAuthorities())
                .isNotEmpty()
                .hasSize(user.getRoles().size())
                .asList().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()));

        verify(myUserDetailsService, times(1)).loadUserByUsername(email);
    }

    @Test
    void jwtTokenVerifierFilterShouldNotAuthenticateWithInvalidJwtToken(CapturedOutput output) throws ServletException, IOException {
        // given
        String email = "email123@gmail.com";
        String jwtToken = securityUtils.generateJwtToken(email) + "xx";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(securityUtils.getJwtCookieName(), jwtToken));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        jwtTokenVerifierFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        assertThat(output.toString()).contains(
                "Other JWT exception: JWT signature does not match locally computed signature." +
                        " JWT validity cannot be asserted and should not be trusted."
        );
        verify(myUserDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void jwtTokenVerifierFilterShouldNotAuthenticateWithoutJwtCookie() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        jwtTokenVerifierFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        verify(myUserDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void jwtTokenVerifierFilterShouldNotAuthenticateWhenUserNotExists(CapturedOutput output) throws ServletException, IOException {
        // given
        String email = "email123@gmail.com";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(securityUtils.getJwtCookieName(), securityUtils.generateJwtToken(email)));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        given(myUserDetailsService.loadUserByUsername(email)).willReturn(null);

        // when
        jwtTokenVerifierFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        assertThat(output.toString()).contains(
                "JwtTokenVerifierFilter Exception Cannot invoke \"" +
                "org.springframework.security.core.userdetails.UserDetails.getAuthorities()\"" +
                " because \"userDetails\" is null"
        );
        verify(myUserDetailsService, times(1)).loadUserByUsername(email);
    }
}