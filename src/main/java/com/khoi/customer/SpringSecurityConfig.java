package com.khoi.customer;


import com.khoi.customer.service.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  //private IUserService userService;
  private UserServiceImpl userService;
  private String username;
  private String password;


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests().antMatchers("/customer/login").permitAll()
        .anyRequest().authenticated()
        .and().formLogin().loginPage("/customer/login").defaultSuccessUrl("/customer/findAll")
        .usernameParameter("username")
        .passwordParameter("password")
        .and().httpBasic()
        .and().sessionManagement().disable();
  }

  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("hiepsi1211").password(encoder().encode("123456"))
        .roles("USER");
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/*.css");
    web.ignoring().antMatchers("/*.js");
  }
}
