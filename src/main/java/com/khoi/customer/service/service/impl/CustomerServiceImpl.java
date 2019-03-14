package com.khoi.customer.service.service.impl;

import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.CheckoutData;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.service.ICustomerService;
import com.khoi.orderproto.CreateOrderRequest;
import com.khoi.orderproto.CreateOrderResponse;
import com.khoi.orderproto.OrderServiceGrpc;
import com.khoi.proto.GetPriceRequest;
import com.khoi.proto.GetPriceResponse;
import com.khoi.proto.PriceServiceGrpc;
import com.khoi.stockproto.StockServiceGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer> implements
    ICustomerService {

  @Qualifier("priceService")
  private final PriceServiceGrpc.PriceServiceBlockingStub priceService;

  @Qualifier("stockService")
  private final StockServiceGrpc.StockServiceBlockingStub stockService;

  @Qualifier("orderService")
  private final OrderServiceGrpc.OrderServiceBlockingStub orderService;

  public CustomerServiceImpl(OrderServiceGrpc.OrderServiceBlockingStub orderService,
      PriceServiceGrpc.PriceServiceBlockingStub priceService,
      StockServiceGrpc.StockServiceBlockingStub stockService) {
    this.orderService = orderService;
    this.priceService = priceService;
    this.stockService = stockService;
  }

  @Override
  public Boolean createOrder(Checkout checkout) {
    CreateOrderResponse rs = orderService.createOrder(
        CreateOrderRequest.newBuilder().setCustomerId(checkout.getCustomer_id()).build());
    if (rs.getOrderId() > 0) {
      for (CheckoutData data : checkout.getProducts()) {
        GetPriceResponse priceResponse = priceService
            .getPrice(GetPriceRequest.newBuilder().setProductId(data.getProduct_id()).build());
      }
    } else {
      return false;
    }
    return false;
  }
}
