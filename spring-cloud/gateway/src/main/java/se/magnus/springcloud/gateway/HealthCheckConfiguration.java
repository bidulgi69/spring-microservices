package se.magnus.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class HealthCheckConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
    private final WebClient.Builder builder;
    private WebClient client;

    @Autowired
    public HealthCheckConfiguration(WebClient.Builder builder) {
        this.builder = builder;
    }

    @Bean
    ReactiveHealthContributor coreServices() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
        registry.put("product", () -> getHealth("http://product"));
        registry.put("recommendation", () -> getHealth("http://recommendation"));
        registry.put("review", () -> getHealth("http://review"));
        //  health check moved into edge server,
        //  so it is needed to add product-composite server's health check
        registry.put("product-composite", () -> getHealth("http://product-composite"));
        return CompositeReactiveHealthContributor.fromMap(registry);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }

    private WebClient getWebClient() {
        if (client == null)
            client = builder.build();
        return client;
    }

}
