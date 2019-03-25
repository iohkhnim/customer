package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.User;

public interface IUserService extends IBaseService<User, Integer> {

  /**
   * <p>This methods provide customer's information belongs to username provided</p>
   *
   * @param username Username provided by customer
   * @return All information of that customer
   */
  int getCustomerIdByUsername(String username);
}
