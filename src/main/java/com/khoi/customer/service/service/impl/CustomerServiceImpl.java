package com.khoi.customer.service.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.CheckoutData;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.dto.TrackingOrderDetails;
import com.khoi.customer.service.ICustomerService;
import com.khoi.customer.service.IUserService;
import com.khoi.orderproto.CheckoutDataProto;
import com.khoi.orderproto.CreateOrderRequest;
import com.khoi.orderproto.CreateOrderResponse;
import com.khoi.orderproto.GetOrdersRequest;
import com.khoi.orderproto.GetOrdersResponse;
import com.khoi.orderproto.OrderServiceGrpc;
import com.khoi.orderproto.TrackingOrderDetailsRequest;
import com.khoi.orderproto.TrackingOrderDetailsResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer>
    implements ICustomerService {

  @Qualifier("orderService")
  private final OrderServiceGrpc.OrderServiceBlockingStub orderService;
  @Autowired
  private IUserService userService;

  public CustomerServiceImpl(OrderServiceGrpc.OrderServiceBlockingStub orderService) {
    this.orderService = orderService;
  }

  /**
   *
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

  /**
   *<p>This method helps user to track their order
   * User can only track their own order</p>
   * @param username Currently logged in user
   * @param order_id Order id that user wishes to track
   * @return An order with information that match provided order id and all the order items belong to that order
   */
  @Override
  public TrackingOrderDetails trackingOrderDetails(String username, int order_id) {
    int customer_id = userService.getCustomerIdByUsername(username);
    TrackingOrderDetailsResponse response = orderService.trackingOrderDetails(
        TrackingOrderDetailsRequest.newBuilder().setCustomerId(customer_id).setOrderId(order_id)
            .build());
    if (response.getOrder() != null && response.getOrderItem(0) != null) {
      //map TrackingOrderDetails and TrackingOrderDetailsResponse
      TrackingOrderDetails trackingOrderDetails = new TrackingOrderDetails();
      trackingOrderDetails.setOrder(new JsonFormat().printToString(response.getOrder()));
      List<String> list = response.getOrderItemList().stream()
          .map(s -> new JsonFormat().printToString(s)).collect(
              Collectors.toList());
      trackingOrderDetails.setOrderDetails(list);
      return trackingOrderDetails;
    } else {
      return null;
    }
  }

  /**
   *
   * @param checkout Contains customer id and list of products
   * @return A boolean value according to the result of creating this order
   */
  @Override
  public Boolean createOrder(Checkout checkout) {
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
                .setCustomerId(checkout.getCustomer_id())
                .addAllCheckoutDataProto(checkoutDataProtos)
                .build());
    return rs.getOrderId() > 0;
  }

  /**
   *
   * @param username Username of currently logged in customer
   * @return All orders were placed by provided customer
   */
  @Override
  public List<String> trackingOrders(String username) {
    int customer_id = userService.getCustomerIdByUsername(username);

    List<GetOrdersResponse> ordersResponsesList = new ArrayList<>();

    //get order list
    Iterable<GetOrdersResponse> iterableResponse = toIterable(orderService
        .getOrders(GetOrdersRequest.newBuilder().setCustomerId(customer_id).build()));

    iterableResponse.forEach(ordersResponsesList::add);

    List<String> ordersResponseStringList = ordersResponsesList.stream()
        .map(s -> new JsonFormat().printToString(s)).collect(
            Collectors.toList());
    return ordersResponseStringList;
  }
}
