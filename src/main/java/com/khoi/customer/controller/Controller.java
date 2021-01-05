package com.khoi.customer.controller;

import com.khoi.customer.dto.*;
import com.khoi.customer.service.ICustomerService;
import com.khoi.customer.service.IUserService;
import com.khoi.customer.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
@RequestMapping("customer")
public class Controller {

  @Autowired ICustomerService customerService;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private IUserService userService;

  @Autowired
  @Qualifier("customUserDetailsService")
  private UserDetailsService customUserDetailsService;

  @Autowired
  JwtService jwtService;

  /**
   * An API endpoint (/customer/create) with method POST for creating Customer
   *
   * @param customer Information of the new customer in Customer type
   * @return Http status
   */
  @PostMapping("create")
  public ResponseEntity<String> create(@RequestBody Customer customer) {
    int id = customerService.create(customer);
    if (id != 0) {
      return new ResponseEntity<String>(String.valueOf(id), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<String>(String.valueOf(id), HttpStatus.CONFLICT);
    }
  }

  /**
   * An API endpoint (/customer/update) with method PUT for updating Customer
   *
   * @param customer Information of the customer to be updated in Customer type
   * @return Http status
   */
  @PutMapping("update")
  public ResponseEntity<Void> update(@RequestBody Customer customer) {
    Boolean flag = customerService.update(customer);
    if (flag.equals(true)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  /**
   * An API endpoint (/customer/findById/{id}) with method GET for getting Customer information of
   * provided customer ID
   *
   * @param id Information of the customer to be updated in Customer type
   * @return Customer information in JSON
   */
  @GetMapping("findById/{id}")
  public ResponseEntity<Customer> findByid(@PathVariable("id") int id) {
    Customer obj = customerService.findByid(id);
    return new ResponseEntity<Customer>(obj, HttpStatus.OK);
  }

  /**
   * An API endpoint (/customer/findAll) with method GET for getting all Customers information
   *
   * @return All customers information
   */
  @GetMapping("findAll")
  public ResponseEntity<List<Customer>> findAll() {
    return new ResponseEntity<List<Customer>>(customerService.findAll(), HttpStatus.OK);
  }

  /**
   * An API endpoint (/customer/delete/{id}) with method DELETE for deleting a customer
   *
   * @param id Id of the customer to be deleted
   * @return Http status
   */
  @DeleteMapping("delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") int id) {
    if (customerService.delete(id)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  /**
   * An API endpoint (/customer/checkout) with method POST for checking out an order
   *
   * @param data Contains customer ID, list of products and amount
   * @return Https status
   */
  @PostMapping("checkout")
  public ResponseEntity<String> checkout(@RequestBody Checkout data) {
    int orderId = customerService.createOrder(data);
    if (orderId > 0) {
      return new ResponseEntity<String>(String.valueOf(orderId), HttpStatus.OK);
    } else {
      return new ResponseEntity<String>("Error", HttpStatus.CONFLICT);
    }
  }

  /**
   * An API endpoint (/customer/orders) with method GET for getting all orders information of
   * currently logged in customer
   *
   * @return All orders information of logged in customer
   */
  @GetMapping("orders")
  public ResponseEntity<List<String>> getOrders() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<List<String>>(
        customerService.trackingOrders(username), HttpStatus.OK);
  }

  /**
   * An API endpoint (/customer/order/{order-id}) with method GET for getting a order information
   * and all order items belong to that order of currently logged in customer
   *
   * @param order_id ID of an order placed by logged customer
   * @return An order information and all order items belong to it of logged in customer
   */
  @GetMapping("order/{order_id}")
  public ResponseEntity<TrackingOrderDetails> trackingOrderDetails(
      @PathVariable("order_id") int order_id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    return new ResponseEntity<TrackingOrderDetails>(
        customerService.trackingOrderDetails(username, order_id, role), HttpStatus.OK);
  }

  @PostMapping("register")
  public ResponseEntity<String> register(@RequestBody User user) {
    int id = userService.create(user);
    if (id != 0) {
      return new ResponseEntity<String>(String.valueOf(id), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<String>(String.valueOf(id), HttpStatus.CONFLICT);
    }
  }

  /**
   * An API endpoint (/customer/login) with method POST for validating given username and password
   *
   * @param loginData Contain username and password provided by customer
   * @return An access token
   */
  @PostMapping("login")
  public ResponseEntity<UserTransfer> login(@RequestBody LoginData loginData) {
    try {
      String username = loginData.getUsername();
      String password = loginData.getPassword();

      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(username, password);
      Authentication authentication = this.authenticationManager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      // CustomUser customUser = this.customUserDetailsService.loadUserByUsername(username);
      UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

      List<String> roles = new ArrayList<>();

      for (GrantedAuthority authority : userDetails.getAuthorities()) {
        roles.add(authority.toString());
      }

      return new ResponseEntity<UserTransfer>(
          new UserTransfer(
              userDetails.getUsername(), roles, jwtService.generateTokeLogin(userDetails.getUsername()), HttpStatus.OK),
          HttpStatus.OK);

    } catch (BadCredentialsException bce) {
      return new ResponseEntity<UserTransfer>(new UserTransfer(), HttpStatus.NO_CONTENT);

    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
  }
}
