package com.restAPI.restAPI.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restAPI.restAPI.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import org.json.HTTP;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;


@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request from {}", request.getServletPath());
        log.info("Request method is {}", request.getMethod());
        if (request.getServletPath().equals("/api/oauth/access") || (request.getServletPath().equals("/api/user") && request.getMethod().equals("POST"))) {
            filterChain.doFilter(request, response);
        }
        else{
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try{
                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm = Utility.getAlgorithm();
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodeJWT = verifier.verify(token);
                    String username = decodeJWT.getSubject();
                    String[] roles = decodeJWT.getClaim("roles").asArray(String.class);
                    log.info("username {}, roles {}", username, roles);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role->authorities.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                }
                catch(Exception e){
                    log.error("Error logging in : {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
            else{
                filterChain.doFilter(request, response);
            }
        }
    }
}
