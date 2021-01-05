package com.khoi.customer.config;

import com.khoi.customer.handler.CustomAccessDeniedHandler;
import com.khoi.customer.handler.JwtAuthenticationTokenFilter;
import com.khoi.customer.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("customUserDetailsService")
  private UserDetailsService customUserDetailsService;

  @Bean
  public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
    jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager());
    return jwtAuthenticationTokenFilter;
  }

  @Bean
  public RestAuthenticationEntryPoint restServicesEntryPoint() {
    return new RestAuthenticationEntryPoint();
  }

  @Bean
  public CustomAccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // String [] methodSecured={"/customer/*"};

    http.csrf().disable();

    http.authorizeRequests().antMatchers("/customer/login").permitAll();

    http.antMatcher("/customer/**")
        .httpBasic()
        .authenticationEntryPoint(restServicesEntryPoint())
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
       /* .antMatchers(HttpMethod.GET, "/customer/**")
        .access("hasRole('0') or hasRole('1') or hasRole('2') or hasRole('3')")
        .antMatchers(HttpMethod.POST, "/customer/**")
        .access("hasRole('0') or hasRole('1') or hasRole('2') or hasRole('3')")
        .antMatchers(HttpMethod.DELETE, "/customer/**")
        .access("hasRole('0') or hasRole('1') or hasRole('2')")*/
        .and()
        .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling()
        .accessDeniedHandler(customAccessDeniedHandler());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder
        .userDetailsService(customUserDetailsService)
        .passwordEncoder(bCryptPasswordEncoder());
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
