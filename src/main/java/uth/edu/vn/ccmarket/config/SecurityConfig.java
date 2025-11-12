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


    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            if (authorities.contains(new SimpleGrantedAuthority("ROLE_CC_BUYER"))) {
                response.sendRedirect("/marketplace"); // người mua về marketplace
            } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_EV_OWNER"))) {
                response.sendRedirect("/dashboard");   // chủ xe về dashboard
            } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_CVA"))) {
                response.sendRedirect("/cva/requests"); // CVA về trang quản lý yêu cầu kiểm định
            } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                response.sendRedirect("/admin");        // admin dashboard
            } else {
                response.sendRedirect("/");             // mặc định về home
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationSuccessHandler successHandler) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // public endpoints
                .requestMatchers("/", "/css/**", "/js/**",
                        "/register", "/register-buyer",
                        "/login", "/marketplace", "/h2-console/**",
                        "/market/ai/suggest"
                ).permitAll()

                // EV Owner pages/APIs
                .requestMatchers("/dashboard/**", "/trips/**", "/listings/create", "/owner/**")
                    .hasRole("EV_OWNER")

                // Buyer + Owner có thể mua
                .requestMatchers("/buy")
                    .hasAnyRole("EV_OWNER", "CC_BUYER")

                // CVA zone
                .requestMatchers("/cva/**")
                    .hasRole("CVA")

                // Admin zone
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                // tải certificate cần login
                .requestMatchers("/transactions/*/certificate")
                    .authenticated()

                // tất cả phần còn lại yêu cầu login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        // cấu hình CSRF và frame cho H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/trips/upload"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
