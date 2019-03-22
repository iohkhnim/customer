package com.khoi.customer.config;

import com.khoi.orderproto.OrderServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  private final String orderServiceEndpoint = "localhost:6585";

  /**
   * <p>Create a channel to communicate with gRPC server</p>
   * @return order Channel for gRPC purpose
   */
  @Bean(name = "orderChannel")
  Channel orderChannel() {
    return ManagedChannelBuilder.forTarget(orderServiceEndpoint).usePlaintext().build();
  }

  /**
   *  <p>Create a gRPC service instance to use provided methods</p>
   * @param orderChannel Channel for order gRPC server
   * @return order service for gRPC server
   */
  @Bean(name = "orderService")
  @Qualifier("orderChannel")
  OrderServiceGrpc.OrderServiceBlockingStub orderService(Channel orderChannel) {
    return OrderServiceGrpc.newBlockingStub(orderChannel);
  }
}
