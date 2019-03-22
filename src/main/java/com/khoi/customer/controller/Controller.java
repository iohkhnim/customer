package com.khoi.customer.controller;

import com.khoi.customer.config.TokenUtil;
import com.khoi.customer.dto.*;
import com.khoi.customer.service.ICustomerService;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@RequestMapping("customer")
public class Controller {

  @Autowired
  ICustomerService customerService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  @Qualifier("customUserDetailsService")
  private UserDetailsService customUserDetailsService;

  @PostMapping("create")
  public ResponseEntity<Void> create(@RequestBody Customer customer) {
    Boolean flag = customerService.create(customer);
    if (flag.equals(true)) {
      return new ResponseEntity<Void>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  @PutMapping("update")
  public ResponseEntity<Void> update(@RequestBody Customer customer) {
    Boolean flag = customerService.update(customer);
    if (flag.equals(true)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  @GetMapping("findById/{id}")
  public ResponseEntity<Customer> findByid(@PathVariable("id") int id) {
    Customer obj = customerService.findByid(id);
    return new ResponseEntity<Customer>(obj, HttpStatus.OK);
  }

  @GetMapping("findAll")
  public ResponseEntity<List<Customer>> findAll() {
    return new ResponseEntity<List<Customer>>(customerService.findAll(), HttpStatus.OK);
  }

  @DeleteMapping("delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") int id) {
    if (customerService.delete(id)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  @PostMapping("checkout")
  public ResponseEntity<Void> checkout(@RequestBody Checkout data) {
    if (customerService.createOrder(data)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  @GetMapping("orders")
  public ResponseEntity<List<String>> getOrders() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<List<String>>(customerService.trackingOrders(username),
        HttpStatus.OK);
  }

  @GetMapping("order/{order-id}")
  public ResponseEntity<TrackingOrderDetails> trackingOrderDetails(
      @PathVariable("order-id") int order_id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<TrackingOrderDetails>(
        customerService.trackingOrderDetails(username, order_id), HttpStatus.OK);
  }
  @PostMapping("login")
  public ResponseEntity<UserTransfer> login (@RequestBody LoginData loginData) {
    try {
      String username = loginData.getUsername();
      String password = loginData.getPassword();

      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
      Authentication authentication = this.authenticationManager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

      List<String> roles = new ArrayList<>();

      for (GrantedAuthority authority : userDetails.getAuthorities()) {
        roles.add(authority.toString());
      }

      return new ResponseEntity<UserTransfer>(new UserTransfer(userDetails.getUsername(), roles,
              TokenUtil.createToken(userDetails), HttpStatus.OK), HttpStatus.OK);

    } catch (BadCredentialsException bce) {
      return new ResponseEntity<UserTransfer>(new UserTransfer(), HttpStatus.NO_CONTENT);

    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
  }
}
