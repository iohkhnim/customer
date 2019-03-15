package com.khoi.customer.service.service.impl;

import com.khoi.basecrud.service.service.impl.BaseServiceImpl;
import com.khoi.customer.dto.Checkout;
import com.khoi.customer.dto.CheckoutData;
import com.khoi.customer.dto.Customer;
import com.khoi.customer.service.ICustomerService;
import com.khoi.orderproto.CreateOrderItemRequest;
import com.khoi.orderproto.CreateOrderItemResponse;
import com.khoi.orderproto.CreateOrderRequest;
import com.khoi.orderproto.CreateOrderResponse;
import com.khoi.orderproto.OrderServiceGrpc;
import com.khoi.proto.GetPriceRequest;
import com.khoi.proto.GetPriceResponse;
import com.khoi.proto.PriceServiceGrpc;
import com.khoi.stockproto.GetBestStockRequest;
import com.khoi.stockproto.GetBestStockResponse;
import com.khoi.stockproto.StockServiceGrpc;
import com.khoi.stockproto.SubtractRequest;
import com.khoi.stockproto.SubtractResponse;
import java.util.List;
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

    //create an order
    CreateOrderResponse rs = orderService.createOrder(
        CreateOrderRequest.newBuilder().setCustomerId(checkout.getCustomer_id()).build());
    List<CheckoutData> list = checkout.getProducts();
    if (rs.getOrderId() > 0) { //check if order is successfully created
      for (CheckoutData data : list) {
        GetBestStockResponse bestStock = stockService.getBestStock(
            GetBestStockRequest.newBuilder().setProductId(data.getProduct_id())
                .setAmount(data.getAmount()).build());
        if (bestStock.getStockId() <= 0) { //if there's not enough stock
          continue;
        }
        GetPriceResponse priceResponse = priceService
            .getPrice(GetPriceRequest.newBuilder().setProductId(data.getProduct_id()).build());

        //create order_item
        CreateOrderItemResponse orderItemResponse = orderService
            .createOrderItem(CreateOrderItemRequest.newBuilder().setOrderId(rs.getOrderId())
                .setProductId(data.getProduct_id()).setAmount(data.getAmount())
                .setPrice(priceResponse.getPrice()).setStockId(bestStock.getStockId()).build());
        //subtract stock
        SubtractResponse subtractResponse = stockService.subtract(
            SubtractRequest.newBuilder().setStockId(bestStock.getStockId())
                .setAmount(data.getAmount()).build());
        if (subtractResponse.getStockId() > 1) { //subtract is completed
          continue;
        } else {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
