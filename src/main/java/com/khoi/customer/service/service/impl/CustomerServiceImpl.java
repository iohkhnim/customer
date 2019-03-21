package com.khoi.customer.service.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.CheckoutData;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.service.ICustomerService;
import com.khoi.customer.service.IUserService;
import com.khoi.orderproto.CheckoutDataProto;
import com.khoi.orderproto.CreateOrderRequest;
import com.khoi.orderproto.CreateOrderResponse;
import com.khoi.orderproto.GetOrdersRequest;
import com.khoi.orderproto.GetOrdersResponse;
import com.khoi.orderproto.OrderServiceGrpc;
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

  private static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        return iterator;
      }
    };
  }

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
    return  ordersResponseStringList;
  }
}
