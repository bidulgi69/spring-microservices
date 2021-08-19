package se.magnus.microservices.composite.product;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import se.magnus.microservices.composite.product.jwt.JwtTokenAuthenticationFilter;
import se.magnus.microservices.composite.product.jwt.JwtTokenProvider;

import java.util.Collections;

import static org.springframework.http.HttpMethod.*;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity   //  enable @preauthorize
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                     JwtTokenProvider jwtTokenProvider,
                                                     ReactiveAuthenticationManager authenticationManager) {
        return http
                .authenticationManager(authenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf().disable()
                .formLogin().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(GET, "/product-composite/**").permitAll()
                .pathMatchers(POST, "/product-composite/**").permitAll()
                .pathMatchers(DELETE, "/product-composite/**").hasAuthority("SCOPE:WRITE")
                .anyExchange().authenticated()
                .and()
                .addFilterAt(new JwtTokenAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.just(new User(username, "pwd", Collections.emptyList()));
    }


    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
