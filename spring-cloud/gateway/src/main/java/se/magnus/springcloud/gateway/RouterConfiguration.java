package se.magnus.springcloud.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfiguration {
    private final String eurekaServer;
    private final String configServer;
    private final GatewayFilter filter;

    @Autowired
    public RouterConfiguration(@Value("${app.eureka-server:eureka}") String eurekaServer,
                               @Value("${app.config-server:config-server}") String configServer,
                               JwtAuthenticationFilter filter) {
        this.eurekaServer = eurekaServer;
        this.configServer = configServer;
        //  if you want to apply filter to every route path,
        //  custom filter class needs to implement GlobalFilter, OrderedFilter
        this.filter = filter.apply(new JwtAuthenticationFilter.Config());
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-composite", r -> r
                        .path("/product-composite/**")
                        .filters(f -> f.filter(filter, Integer.MAX_VALUE).addResponseHeader("Filtered", "YES"))
                        .uri("lb://product-composite"))
                .route("eureka-api", r -> r
                        .path("/eureka/api/(?<segment>.*")
                        .filters(f -> f.setPath("/eureka/(?<segment>.*"))
                        .uri(String.format("http://%s:8761", eurekaServer)))
                .route("eureka-web-start", r -> r
                        .path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri(String.format("http://%s:8761", eurekaServer)))
                .route("eureka-web-other", r -> r
                        .path("/eureka/**")
                        .uri(String.format("http://%s:8761", eurekaServer)))
                .route("config-server", r -> r
                        .path("/config/(?<segment>.*)")
                        .filters(f -> f.setPath("/config/(?<segment>.*"))
                        .uri(String.format("http://%s:8888", configServer)))
                .route("host_route_200", r -> r.host("i.feel.lucky:8080")
                        .and()
                        .path("/headerrouting/**")
                        .filters(f -> f.setPath("/200").filter(filter, Integer.MIN_VALUE))
//                                .filter(filter, Integer.MAX_VALUE))  //  custom filter applied
                        .uri("http://httpstat.us"))
                .route("host_route_418", r -> r.host("im.a.teapot:8080")
                        .and()
                        .path("/headerrouting/**")
                        .filters(f -> f.setPath("/418"))
                        .uri("http://httpstat.us"))
                .route("host_route_501", r -> r
                        .path("/headerrouting/**")
                        .filters(f -> f.setPath("/501"))
                        .uri("http://httpstat.us"))
                .build();
    }
}
