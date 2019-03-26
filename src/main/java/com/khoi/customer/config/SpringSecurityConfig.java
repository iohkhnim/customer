package com.khoi.customer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("customUserDetailsService")
  private UserDetailsService customUserDetailsService;

  @Autowired
  @Qualifier("authTokenConfig")
  private AuthTokenConfig authTokenConfig;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // String [] methodSecured={"/customer/*"};

    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/",
            "/customer/login") //, "/customer/login/oauth2/client/google") needed if customize login page
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login();
        //needed if customize login page
        /*.loginPage("/customer/login/oauth2/client/google")
        .authorizationEndpoint()
        .baseUri("/oauth2/authorize/google")
        .authorizationRequestRepository(authorizationRequestRepository());*/
    http.apply(authTokenConfig);
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

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
  authorizationRequestRepository() {

    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }
}
