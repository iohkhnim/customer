package com.khoi.customer.controller;

import com.khoi.customer.config.TokenUtil;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.LoginData;
import com.khoi.customer.dto.TrackingOrderDetails;
import com.khoi.customer.dto.UserTransfer;
import com.khoi.customer.service.ICustomerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

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

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  /**
   * <p>An API endpoint (/customer/create) with method POST for creating Customer</p>
   *
   * @param customer Information of the new customer in Customer type
   * @return Http status
   */
  @PostMapping("create")
  public ResponseEntity<Void> create(@RequestBody Customer customer) {
    Boolean flag = customerService.create(customer);
    if (flag.equals(true)) {
      return new ResponseEntity<Void>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  /**
   * <p>An API endpoint (/customer/update) with method PUT for updating Customer</p>
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
   * <p>An API endpoint (/customer/findById/{id}) with method GET for getting Customer information
   * of provided customer ID</p>
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
   * <p>An API endpoint (/customer/findAll) with method GET for getting all Customers information
   * </p>
   *
   * @return All customers information
   */
  @GetMapping("findAll")
  public ResponseEntity<List<Customer>> findAll() {
    return new ResponseEntity<List<Customer>>(customerService.findAll(), HttpStatus.OK);
  }

  /**
   * <p>An API endpoint (/customer/delete/{id}) with method DELETE for deleting a customer</p>
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
   * <p>An API endpoint (/customer/checkout) with method POST for checking out an order</p>
   *
   * @param data Contains customer ID, list of products and amount
   * @return Https status
   */
  @PostMapping("checkout")
  public ResponseEntity<Void> checkout(@RequestBody Checkout data) {
    if (customerService.createOrder(data)) {
      return new ResponseEntity<Void>(HttpStatus.OK);
    } else {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }
  }

  /**
   * <p>An API endpoint (/customer/orders) with method GET for getting all orders information of
   * currently logged in customer</p>
   *
   * @return All orders information of logged in customer
   */
  @GetMapping("orders")
  public ResponseEntity<List<String>> getOrders() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<List<String>>(customerService.trackingOrders(username),
        HttpStatus.OK);
  }

  /**
   * <p>An API endpoint (/customer/order/{order-id}) with method GET for getting a order
   * information and all order items belong to that order of currently logged in customer</p>
   *
   * @param order_id ID of an order placed by logged customer
   * @return An order information and all order items belong to it of logged in customer
   */
  @GetMapping("order/{order_id}")
  public ResponseEntity<TrackingOrderDetails> trackingOrderDetails(
      @PathVariable("order_id") int order_id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return new ResponseEntity<TrackingOrderDetails>(
        customerService.trackingOrderDetails(username, order_id), HttpStatus.OK);
  }

  /**
   * <p>An API endpoint (/customer/login) with method POST for validating given username and
   * password</p>
   *
   * @param loginData Contain username and password provided by customer
   * @return An access token
   */
  @PostMapping("login")
  public ResponseEntity<UserTransfer> login(@RequestBody LoginData loginData) {
    try {
      String username = loginData.getUsername();
      String password = loginData.getPassword();

      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
          password);
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

  @GetMapping("successful")
  public ResponseEntity<String> successfulLoginWithGoogle(
      OAuth2AuthenticationToken authenticationToken) {
    OAuth2AuthorizedClient client = authorizedClientService
        .loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(),
            authenticationToken.getName());

    String userInfoEndpointUri = client.getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUri();

    if (!StringUtils.isEmpty(userInfoEndpointUri)) {
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
          .getTokenValue());
      HttpEntity entity = new HttpEntity("", headers);
      ResponseEntity<Map> responseEntity =
          restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
      Map userAttributes = responseEntity.getBody();
      return new ResponseEntity<>("Hello " + userAttributes.get("name").toString(), HttpStatus.OK);
    }

    return new ResponseEntity<>("", HttpStatus.OK);
  }

  /*@GetMapping("login/oauth/client/google")
  public ResponseEntity<String> loginWithGoogle(
      @RequestBody OAuth2AuthenticationToken authenticationToken) {
    String name = authenticationToken.getName();
    return new ResponseEntity<>(name, HttpStatus.OK);
  }*/
}
