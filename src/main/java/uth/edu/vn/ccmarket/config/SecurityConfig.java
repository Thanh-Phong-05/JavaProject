package uth.edu.vn.ccmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/css/**", "/js/**", "/register", "/login",
                                                                "/marketplace",
                                                                "/h2-console/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login").defaultSuccessUrl("/dashboard", true))
                                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

                http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
                http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
                return http.build();
        }
}
