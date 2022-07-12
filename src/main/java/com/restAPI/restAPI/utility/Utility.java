package com.restAPI.restAPI.utility;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter @Slf4j
public class Utility {
    private static final Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
    public static String getUsernameFromToken(String token){
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodeJWT = verifier.verify(token);
            String username = decodeJWT.getSubject();
            return username;
        }
        catch (Exception e){
            log.error("Error occur while get username from token {}", token);
        }
        return null;
    }

    public static String getIssuerFromToken(String token){
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodeJWT = verifier.verify(token);
            String username = decodeJWT.getIssuer();
            return username;
        }
        catch (Exception e){
            log.error("Error occur while get issuer(req url) from token {}", token);
        }
        return null;

    }

    public static Algorithm getAlgorithm(){
        return algorithm;
    }


}
