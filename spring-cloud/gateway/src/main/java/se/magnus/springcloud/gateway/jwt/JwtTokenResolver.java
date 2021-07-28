package se.magnus.springcloud.gateway.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenResolver {
    //  TODO    get authentications from external source
    public boolean resolve(String token) {
        return token != null && token.startsWith("Bearer ");
    }
}
