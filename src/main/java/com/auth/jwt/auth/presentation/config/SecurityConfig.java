package com.auth.jwt.auth.presentation.config;

import com.auth.jwt.auth.application.AuthenticationService;
import com.auth.jwt.auth.presentation.filter.AuthenticationFilter;
import com.auth.jwt.auth.presentation.filter.AuthorizationFilter;
import com.auth.jwt.auth.presentation.filter.RefreshTokenFilter;
import com.auth.jwt.auth.presentation.handler.CustomAccessDeniedHandler;
import com.auth.jwt.auth.presentation.utils.AuthResponseSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final CorsProperties corsProperties;
  private final AuthorizationFilter authorizationFilter;
  private final RefreshTokenFilter refreshTokenFilter;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  private final AuthenticationService authenticationService;
  private final AuthResponseSender authResponseSender;
  private final ObjectMapper objectMapper;
  private final AuthenticationConfiguration authenticationConfiguration;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(customAccessDeniedHandler));

    http.authorizeHttpRequests(
        requests ->
            requests
                .requestMatchers(HttpMethod.POST, "/errors")
                .permitAll()
                // register auth
                .requestMatchers(HttpMethod.POST, "/login", "/refresh-token")
                .permitAll()
                // register user
                .requestMatchers(HttpMethod.POST, "/users")
                .permitAll()
                // any request
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated());

    http.addFilterBefore(refreshTokenFilter, AuthenticationFilter.class);
    http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationFilter authenticationFilter() throws Exception {
    AuthenticationFilter filter =
        new AuthenticationFilter(authenticationService, authResponseSender, objectMapper);
    filter.setAuthenticationManager(authenticationManager());
    filter.setFilterProcessesUrl("/login");
    return filter;
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
    configuration.setAllowedMethods(corsProperties.getAllowedMethods());
    configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
    configuration.setExposedHeaders(corsProperties.getExposedHeaders());
    configuration.setAllowCredentials(corsProperties.getAllowCredentials());
    configuration.setMaxAge(corsProperties.getMaxAge());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
