package com.example.aggridserversidespringbootexample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Profile({"docker", "dev"})
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${originUrl}")
  private String[] originUrl;
  @Value("${headers}")
  private String[] headers;
  @Value("${methods}")
  private String[] methods;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // отключить базовую авторизацию
    http
        .authorizeRequests()
        .anyRequest()
        .permitAll();
    // включить базовую авторизацию
//     http
//         .authorizeRequests()
//         .anyRequest()
//         .authenticated()
//         .and()
//         .httpBasic();

    http
        .cors();

    http
        .csrf()
        .disable();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(Arrays.asList(originUrl));
    configuration.setAllowedMethods(Arrays.asList(methods));
    configuration.setAllowedHeaders(Arrays.asList(headers));
    configuration.setExposedHeaders(Arrays.asList(headers));
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
  }

  @Bean
  UserDetailsService users() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    UserDetails userDetails = User.withDefaultPasswordEncoder()
        .username("user")
        .password("passwd")
        .roles("USER")
        .build();
    manager.createUser(userDetails);
    return manager;
  }
}
