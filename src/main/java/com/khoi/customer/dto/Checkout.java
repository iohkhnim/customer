package com.khoi.customer.dto;

import java.util.List;

public class Checkout {

  public List<CheckoutData> getProducts() {
    return products;
  }

  public void setProducts(List<CheckoutData> products) {
    this.products = products;
  }

  private String username;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  private String address;

  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }

  private List<CheckoutData> products;
}
