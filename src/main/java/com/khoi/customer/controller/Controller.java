package com.khoi.customer.controller;

import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.service.ICustomerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.stereotype.Controller
@RequestMapping("customer")
public class Controller {

  @Autowired
  ICustomerService customerService;

  @Autowired
  private AuthorizationServerTokenServices tokenServices;

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
    return new ResponseEntity<List<String>>(customerService.trackingOrders(username), HttpStatus.OK);
  }

  @RequestMapping(value = "getSomething", method = RequestMethod.GET)
  public String getSection(OAuth2Authentication authentication) {
    /*Map<String, Object> additionalInfo = tokenServices.getAccessToken(authentication).getAdditionalInformation();

    String customInfo = (String) additionalInfo.get("customer_id");*/

    // Play with authorities
    //return customInfo;

    System.out
        .println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
    return "abcxyz";
  }

  /*@PostMapping("login")
  public ResponseEntity<Void> login (@RequestBody LoginData loginData) {
    return new ResponseEntity<Void>(HttpStatus.OK);
  }*/
}
