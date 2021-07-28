package se.magnus.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import se.magnus.springcloud.gateway.jwt.JwtTokenResolver;

import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
    private final JwtTokenResolver resolver;

    public JwtAuthenticationFilter(JwtTokenResolver resolver) {
        super(Config.class);
        this.resolver = resolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            try {
                Object token = Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0);
                if (!resolver.resolve(token.toString())) throw new NullPointerException();
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                return unauthorized(exchange);
            }
            return chain.filter(exchange);
        });
    }

    //  handle 401 (unauthorized)
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    //  handle 406 (not acceptable)
    private Mono<Void> notAcceptable(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
        return response.setComplete();
    }

    public static class Config {

    }
}
