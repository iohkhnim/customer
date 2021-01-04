package com.khoi.customer.dto;

import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {

  private int role;

  public CustomUser(com.khoi.customer.dto.User userDTO) {
    super(userDTO.getUsername(), userDTO.getPassword(), userDTO.getGrantedAuthoritiesList());
    this.setRole(userDTO.getRole());
  }

  public int getRole() {
    return role;
  }

  public void setRole(int role) {
    this.role = role;
  }
}
