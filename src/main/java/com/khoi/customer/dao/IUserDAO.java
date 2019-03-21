package com.khoi.customer.dao;

import com.khoi.basecrud.dao.IBaseDAO;
import com.khoi.customer.dto.User;

public interface IUserDAO extends IBaseDAO<User, Integer> {

  User findByUsername(String username);

  int getCustomerIdByUsername(String usnername);
}
