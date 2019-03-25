package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.TrackingOrderDetails;
import java.util.List;

public interface ICustomerService extends IBaseService<Customer, Integer> {

  /**
   * @param checkout Contains customer id and list of products
   * @return A boolean value according to the result of creating this order
   */
  Boolean createOrder(Checkout checkout);

  /**
   * @param username Username of currently logged in customer
   * @return All orders were placed by provided customer
   */
  List<String> trackingOrders(String username);

  /**
   * <p>This method helps user to track their order
   * User can only track their own order</p>
   *
   * @param username Currently logged in user
   * @param order_id Order id that user wishes to track
   * @return An order with information that match provided order id and all the order items belong
   * to that order
   */
  TrackingOrderDetails trackingOrderDetails(String username, int order_id);
}
