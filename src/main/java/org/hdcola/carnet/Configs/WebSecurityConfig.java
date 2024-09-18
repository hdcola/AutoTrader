package org.hdcola.carnet.Configs;

import org.hdcola.carnet.Handler.OAuth2LoginSuccessHandler;
import org.hdcola.carnet.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public WebSecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/register","/login", "/", "/oauth2/**").permitAll()
                                .requestMatchers("/buyer").hasRole("BUYER")
                                .requestMatchers("/seller").hasRole("SELLER")
                                .requestMatchers("/settings").authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .formLogin(
                        (form) -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .permitAll()
                                .defaultSuccessUrl("/", true)
                )
                .logout(
                        (logout) -> logout.logoutSuccessUrl("/").permitAll()
                );

        return http.build();
    }






}
