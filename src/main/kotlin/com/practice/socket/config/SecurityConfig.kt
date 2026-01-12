package com.practice.socket.config

import com.practice.socket.service.user.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig (
    private var oAuth2UserService: CustomOAuth2UserService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { request -> request
                .requestMatchers("/login", "/oauth2/**", "/error", "/main.css", "/stomp.js", "/mypage.js").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login { oauth -> oauth
                .userInfoEndpoint { userInfo -> userInfo.userService(oAuth2UserService)}
                .defaultSuccessUrl("/", true)
            }
            .logout{ logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            }
            .csrf{ csrf -> csrf.disable() }
            .build()
    }
}