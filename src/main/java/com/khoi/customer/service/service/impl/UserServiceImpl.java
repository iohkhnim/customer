package com.khoi.customer.service.service.impl;

import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dao.IUserDAO;
import com.khoi.customer.dto.User;
import com.khoi.customer.service.IUserService;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Integer> implements IUserService {

  @Autowired
  private IUserDAO userDAO;

  public User getUserDetails(String username) {
    Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
    User user = userDAO.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
    grantedAuthoritiesList.add(grantedAuthority);
    user.setGrantedAuthoritiesList(grantedAuthoritiesList);
    return user;
  }

  public int getCustomerIdByUsername(String username) {
    return userDAO.getCustomerIdByUsername(username);
  }
}
