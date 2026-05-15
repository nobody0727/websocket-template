package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 * 配置HTTP安全策略和密码编码器
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    // JWT认证过滤器
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT令牌）
            .csrf(AbstractHttpConfigurer::disable)
            // 配置会话管理为无状态（使用JWT）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 登录接口允许所有人访问
                .antMatchers("/api/auth/login").permitAll()
                // WebSocket端点允许所有人访问
                .antMatchers("/ws/**").permitAll()
                // 用户管理接口需要ADMIN角色
                .antMatchers("/api/users/**").hasRole("ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            // 将JWT认证过滤器添加到用户名密码认证过滤器之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * 配置密码编码器（BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 获取认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}