package com.khoi.customer.Config;

import com.khoi.orderproto.OrderServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  private final String orderServiceEndpoint = "localhost:6585";

  @Bean(name = "orderChannel")
  Channel orderChannel() {
    return ManagedChannelBuilder.forTarget(orderServiceEndpoint).usePlaintext().build();
  }

  @Bean(name = "orderService")
  @Qualifier("orderChannel")
  OrderServiceGrpc.OrderServiceBlockingStub orderService(Channel orderChannel) {
    return OrderServiceGrpc.newBlockingStub(orderChannel);
  }
}
