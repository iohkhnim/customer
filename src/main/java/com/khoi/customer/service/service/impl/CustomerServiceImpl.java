package com.khoi.customer.service.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.CheckoutData;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.TrackingOrderDetails;
import com.khoi.customer.service.ICustomerService;
import com.khoi.customer.service.IUserService;
import com.khoi.orderproto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer>
    implements ICustomerService {

  @Qualifier("orderService")
  private final OrderServiceGrpc.OrderServiceBlockingStub orderService;

  @Autowired private IUserService userService;
  @Autowired private OAuth2AuthorizedClientService authorizedClientService;

  public CustomerServiceImpl(OrderServiceGrpc.OrderServiceBlockingStub orderService) {
    this.orderService = orderService;
  }

  /**
   * @param iterator Iterator object
   * @param <T> Type of iterator object
   * @return Iterable object with the same type of provided Iterator object
   */
  private static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return iterator;
      }
    };
  }

  /** {@inheritDoc} */
  @Override
  public TrackingOrderDetails trackingOrderDetails(String username, int order_id, String role) {
    int customer_id = userService.getCustomerIdByUsername(username);
    TrackingOrderDetailsResponse response =
        orderService.trackingOrderDetails(
            TrackingOrderDetailsRequest.newBuilder()
                .setCustomerId(customer_id)
                .setOrderId(order_id)
                .setRole(role)
                .build());
    if (response.getOrder() != null && response.getOrderItem(0) != null) {
      // map TrackingOrderDetails and TrackingOrderDetailsResponse
      TrackingOrderDetails trackingOrderDetails = new TrackingOrderDetails();
      trackingOrderDetails.setOrder(new JsonFormat().printToString(response.getOrder()));
      List<String> list =
          response.getOrderItemList().stream()
              .map(s -> new JsonFormat().printToString(s))
              .collect(Collectors.toList());
      trackingOrderDetails.setOrderDetails(list);
      return trackingOrderDetails;
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int createOrder(Checkout checkout) {
    List<CheckoutData> list = checkout.getProducts();
    List<CheckoutDataProto> checkoutDataProtos = new ArrayList<>();

    list.stream()
        .forEach(
            p ->
                checkoutDataProtos.add(
                    CheckoutDataProto.newBuilder()
                        .setProductId(p.getProduct_id())
                        .setAmount(p.getAmount())
                        .build()));
    // create an order
    CreateOrderResponse rs =
        orderService.createOrder(
            CreateOrderRequest.newBuilder()
                .setCustomerId(userService.getCustomerIdByUsername(checkout.getUsername()))
                .setAddress(checkout.getAddress())
                .addAllCheckoutDataProto(checkoutDataProtos)
                .build());
    return rs.getOrderId();
  }

  /** {@inheritDoc} */
  @Override
  public List<String> trackingOrders(String username) {
    int customer_id = userService.getCustomerIdByUsername(username);

    List<GetOrdersResponse> ordersResponsesList = new ArrayList<>();

    // get order list
    Iterable<GetOrdersResponse> iterableResponse =
        toIterable(
            orderService.getOrders(
                GetOrdersRequest.newBuilder().setCustomerId(customer_id).build()));

    iterableResponse.forEach(ordersResponsesList::add);

    List<String> ordersResponseStringList =
        ordersResponsesList.stream()
            .map(s -> new JsonFormat().printToString(s))
            .collect(Collectors.toList());
    return ordersResponseStringList;
  }

  /** {@inheritDoc} */
  @Override
  public String getNameWhenLoginWithGoogle(OAuth2AuthenticationToken authenticationToken) {
    OAuth2AuthorizedClient client =
        authorizedClientService.loadAuthorizedClient(
            authenticationToken.getAuthorizedClientRegistrationId(), authenticationToken.getName());

    String userInfoEndpointUri =
        client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

    if (!StringUtils.isEmpty(userInfoEndpointUri)) {
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
      HttpEntity entity = new HttpEntity("", headers);
      ResponseEntity<Map> responseEntity =
          restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
      Map userAttributes = responseEntity.getBody();
      return userAttributes.get("name").toString();
    } else {
      return "";
    }
  }
}
