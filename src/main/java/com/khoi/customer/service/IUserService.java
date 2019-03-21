package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.User;
import java.util.List;

public interface IUserService extends IBaseService<User, Integer> {

  int getCustomerIdByUsername(String username);
}
