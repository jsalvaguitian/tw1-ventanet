package com.tallerwebi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/* 
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // recursos estáticos
                .anyRequest().permitAll() // si querés login, cambiar a .authenticated()
            )
            .csrf(csrf -> csrf.enable()); // habilita CSRF

        return http.build();
    }

}
*/