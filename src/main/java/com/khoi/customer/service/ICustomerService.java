package com.khoi.customer.service;

import com.khoi.basecrud.service.IBaseService;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.TrackingOrderDetails;
import java.util.List;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface ICustomerService extends IBaseService<Customer, Integer> {

  /**
   * @param checkout Contains customer id and list of products
   * @return A boolean value according to the result of creating this order
   */
  int createOrder(Checkout checkout);

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
  TrackingOrderDetails trackingOrderDetails(String username, int order_id, String role);

  /**
   * <p>This method gets user's name from user information endpoint given by Google</p>
   * @param authenticationToken Access token given by Google
   * @return Return user's name
   */
  // String getNameWhenLoginWithGoogle(OAuth2AuthenticationToken authenticationToken);
}
