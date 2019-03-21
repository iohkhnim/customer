package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.TrackingOrderDetails;
import java.util.List;

public interface ICustomerService extends IBaseService<Customer, Integer> {

  Boolean createOrder(Checkout checkout);

  List<String> trackingOrders(String username);

  TrackingOrderDetails trackingOrderDetails(String username, int order_id);
}
