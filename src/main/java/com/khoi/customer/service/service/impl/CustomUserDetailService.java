package com.khoi.customer.service.service.impl;

import com.khoi.customer.dto.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "customUserDetailsService")
public class CustomUserDetailService implements UserDetailsService {

  @Autowired
  UserServiceImpl userService;

  @Override
  public CustomUser loadUserByUsername(final String username) throws UsernameNotFoundException {
    com.khoi.customer.dto.User userDTO = null;
    try {
      userDTO = userService.getUserDetails(username);
      CustomUser customUser = new CustomUser(userDTO);
      return customUser;
    } catch (Exception e) {
      e.printStackTrace();
      throw new UsernameNotFoundException("User " + username + " not found");
    }
  }
}
