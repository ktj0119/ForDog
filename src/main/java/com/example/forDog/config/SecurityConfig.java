package com.example.forDog.config;

import jakarta.servlet.SessionTrackingMode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // 관리자 페이지 로그인 해야 들어갈 수 있게 설정(TJ)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/manager/**")
                .excludePathPatterns("/manager/login",
                        "/manager/loginProc",
                        "/manager/regist",
                        "/manager/registProc",
                        "/css/**", "/js/**", "/images/**");
    }

    // [다희] 수정 부분 시작
    @Bean // securityFilterChain(보안 규칙 설정)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // "/api/"로 시작되는 모든 url 인증 없이 허용
                        .anyRequest().permitAll() // 나머지 모든 요청도 일단 허용(추가 백엔드 설정에 따라 조절해 주세요)
                );
        return http.build();
    }

    @Bean // corsConfigurationSource(외부 통신 허용 설정)
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*")); // 모든 출처 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 모든 HTTP 메서드 허용
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 위 설정 적용
        return source;
    }

    @Bean // configSession(세션 관리 설정)
    public ServletContextInitializer configSession() {
        return servletContext -> {
            servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
            servletContext.getSessionCookieConfig().setHttpOnly(true);
        };
    }
    // [다희] 수정 부분 종료

}
