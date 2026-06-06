package com.paypal.api_gateway_service;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitterConfig {

    public KeyResolver userKeyResolver(){
        return exchange -> {
          String userId=exchange.getRequest().getHeaders().getFirst("X-User-Id");
          if(userId!=null){
              return Mono.just(userId);
          }
          return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }

}
