package com.khoi.customer.dto;

import com.khoi.basecrud.dto.baseDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "user")
public class User extends baseDTO implements Serializable {

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "customer_id")
  private int customer_id;

  private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getCustomer_id() {
    return customer_id;
  }

  public void setCustomer_id(int customer_id) {
    this.customer_id = customer_id;
  }

  public Collection<GrantedAuthority> getGrantedAuthoritiesList() {
    return grantedAuthoritiesList;
  }

  public void setGrantedAuthoritiesList(
      Collection<GrantedAuthority> grantedAuthoritiesList) {
    this.grantedAuthoritiesList = grantedAuthoritiesList;
  }
}
