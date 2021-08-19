package se.magnus.microservices.composite.product.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "roles";

    public Authentication getAuthentication(String token) {
        User principal = new User("subject", "pwd", Collections.singletonList(new SimpleGrantedAuthority(token)));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}
