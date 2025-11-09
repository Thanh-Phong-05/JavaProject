package uth.edu.vn.ccmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // đieu phoi dang nhap
        @Bean
        public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
                return (request, response, authentication) -> {
                        var authorities = authentication.getAuthorities();

                        if (authorities.contains(new SimpleGrantedAuthority("ROLE_CC_BUYER"))) {
                                // ng mua ve marketplace
                                response.sendRedirect("/marketplace");
                        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_EV_OWNER"))) {
                                // chu xe ve dashboard
                                response.sendRedirect("/dashboard");
                        } else {
                                // về trang chủ
                                response.sendRedirect("/");
                        }
                };
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        AuthenticationSuccessHandler successHandler) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/css/**", "/js/**",
                                                                "/register", "/register-buyer",
                                                                "/login", "/marketplace", "/h2-console/**")
                                                .permitAll()
                                                .requestMatchers("/dashboard/**", "/trips/**", "/listings/create")
                                                .hasRole("EV_OWNER")
                                                .requestMatchers("/buy").hasAnyRole("EV_OWNER", "CC_BUYER")
                                                .requestMatchers("/cva/**", "/admin/**").denyAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")

                                                .successHandler(successHandler))
                                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

                http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/trips/upload"));
                http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
                return http.build();
        }
}