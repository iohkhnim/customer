package com.khoi.customer.dto;

import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {

  public CustomUser(com.khoi.customer.dto.User userDTO) {
    super(userDTO.getUsername(), userDTO.getPassword(), userDTO.getGrantedAuthoritiesList());
  }
}
