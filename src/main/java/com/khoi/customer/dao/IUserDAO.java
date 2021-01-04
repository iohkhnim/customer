package com.khoi.customer.dao;

import com.khoi.basecrud.dao.IBaseDAO;
import com.khoi.customer.dto.User;

public interface IUserDAO extends IBaseDAO<User, Integer> {

  /**
   *  <p>Retrieve user's information with provided username</p>
   * @param username Username of customer
   * @return Customer's information
   */
  User findByUsername(String username);

  /**
   * <p>Retrieve Customer ID of provided username</p>
   *
   * @param username username of customer
   * @return id of that username
   */
  int getCustomerIdByUsername(String username);

}
