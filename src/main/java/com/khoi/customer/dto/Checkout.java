package com.khoi.customer.dto;

import java.util.List;

public class Checkout {

  public int getCustomer_id() {
    return customer_id;
  }

  public void setCustomer_id(int customer_id) {
    this.customer_id = customer_id;
  }

  public List<CheckoutData> getProducts() {
    return products;
  }

  public void setProducts(List<CheckoutData> products) {
    this.products = products;
  }

  private int customer_id;
  private List<CheckoutData> products;
}
