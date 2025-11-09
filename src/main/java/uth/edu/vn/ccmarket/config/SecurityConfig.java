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
                                                // công khai
                                                .requestMatchers("/", "/css/**", "/js/**",
                                                                "/register", "/register-buyer", // <-- Thêm mới
                                                                "/login", "/marketplace", "/h2-console/**")
                                                .permitAll()

                                                // chỉ cho chủ xe
                                                .requestMatchers("/dashboard/**", "/trips/**", "/listings/create")
                                                .hasRole("EV_OWNER") // (hasRole sẽ tự thêm "ROLE_")

                                                // chỉ cho người mua
                                                .requestMatchers("/buy").hasAnyRole("EV_OWNER", "CC_BUYER")

                                                // của admin
                                                .requestMatchers("/cva/**", "/admin/**").denyAll()

                                                // tất cả yêu cầu khác cần xác thực
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login").defaultSuccessUrl("/dashboard", true))
                                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

                http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/trips/upload")); // (Nên thêm
                                                                                                    // /trips/upload)
                http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
                return http.build();
        }
}