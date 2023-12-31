package com.sprta.deliveryproject.config;

import com.sprta.deliveryproject.jwt.JwtAuthenticationFilter;
import com.sprta.deliveryproject.jwt.JwtAuthorizationFilter;
import com.sprta.deliveryproject.jwt.JwtUtil;
import com.sprta.deliveryproject.security.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                .requestMatchers("/").permitAll() // '/' 로 접근 허용
//                                .requestMatchers("/api/members/likes",HttpMethod.GET.name()).permitAll() // '/api/member/' 로 시작하는 GET 요청 허용
                                .requestMatchers("/api/members/**",HttpMethod.POST.name()).permitAll() // '/api/member/' 로 시작하는 POST 요청 허용
                                .requestMatchers("/api/shops/**",HttpMethod.GET.name()).permitAll()
                                .requestMatchers("/view/**").permitAll() //view seurity 설정 전 열어놓음
                                .requestMatchers("/shops/manage").permitAll()
                                //.requestMatchers("/api/shops/**",HttpMethod.GET.name()).permitAll()
                                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 로그인 사용
        //http.formLogin().disable();
        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/members/login-page")
                        //.loginProcessingUrl("/api/member/login")
                        //.defaultSuccessUrl("/")
                        //.failureUrl("/api/member/login-page?error")
                        .permitAll()
        );

        // 필터 관리
        //jwtAuthorizationFilter -> jwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter
        http.addFilterAfter(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}