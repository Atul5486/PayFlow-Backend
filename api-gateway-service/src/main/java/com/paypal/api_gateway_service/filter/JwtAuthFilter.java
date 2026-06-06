package com.paypal.api_gateway_service.filter;

import com.paypal.api_gateway_service.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        String path = exchange.getRequest().getPath().value();
        String normalizedPath = path.replaceAll("/+$", "");

        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod()) || isPublicPath(normalizedPath)) {
            return chain.filter(exchange)
                    .doOnSubscribe(s -> System.out.println("Public/preflight request bypassed auth check"));
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if( authHeader==null || !authHeader.startsWith("Bearer ")){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try{
            String token = authHeader.substring(7);
            Claims claims = JwtUtils.validateToken(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Email", claims.getSubject())
                            .header("X-User-Id", String.valueOf(claims.get("userId")))
                            .header("X-User-Role", (String) claims.get("role"))
                            .build())
                    .build();
            return chain.filter(mutatedExchange);
        }catch (Exception e){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isPublicPath(String normalizedPath) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath ->
                normalizedPath.equals(publicPath) || normalizedPath.startsWith(publicPath + "/"));
    }
}