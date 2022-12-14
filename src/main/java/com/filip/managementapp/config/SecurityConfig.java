package com.filip.managementapp.config;

import com.filip.managementapp.security.JwtAuthenticationEntryPoint;
import com.filip.managementapp.security.filter.JwtTokenVerifierFilter;
import com.filip.managementapp.service.MyUserDetailsService;
import com.filip.managementapp.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    protected static final String[] WHITELISTED_RESOURCE_ENDPOINTS = {
            "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**",
            "/resources/static/**", "/css/**", "/js/**", "/img/**", "/fonts/**",
            "/images/**", "/scss/**", "/vendor/**", "/favicon.ico", "/favicon.png",
            "/manifest.json"
    };

    protected static final String[] WHITELISTED_REACT_ENDPOINTS = {
            "/", "/home*",
            "/login*", "/register*",
            "/users*", "/users/**",
            "/profile*",
            "/products*", "/products/**"
    };

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final MyUserDetailsService myUserDetailsService;
    private final SecurityUtils securityUtils;
    private final AuthenticationConfiguration authConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(
                        new JwtTokenVerifierFilter(securityUtils, myUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .and()
                .csrf().disable()
                .cors()
                    .and()
                .authenticationProvider(authenticationProvider())
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers("/actuator", "/actuator/**").hasAnyRole("ADMIN")
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/api/v1/auth/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                    .antMatchers(WHITELISTED_REACT_ENDPOINTS).permitAll()
                    .antMatchers(WHITELISTED_RESOURCE_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin().loginPage("/login");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
