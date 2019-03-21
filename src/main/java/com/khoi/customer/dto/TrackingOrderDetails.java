package com.khoi.customer.dto;

import java.util.List;

public class TrackingOrderDetails {

  private String Order;
  private List<String> OrderDetails;

  public String getOrder() {
    return Order;
  }

  public void setOrder(String order) {
    Order = order;
  }

  public List<String> getOrderDetails() {
    return OrderDetails;
  }

  public void setOrderDetails(List<String> orderDetails) {
    OrderDetails = orderDetails;
  }
}
