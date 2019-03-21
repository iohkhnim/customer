package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import java.util.List;

public interface ICustomerService extends IBaseService<Customer, Integer> {

  Boolean createOrder(Checkout checkout);

  List<String> trackingOrders(String username);
}
