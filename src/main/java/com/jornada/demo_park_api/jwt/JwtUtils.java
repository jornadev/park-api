package com.jornada.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static String SECRET_KEY = "12345678901234567890123456789012";
    private static final long EXPIRE_DAYS = 0;
    private static final long EXPIRE_HOURS = 0;
    private static final long EXPIRE_MINUTES = 30;

    private JwtUtils() {
    }

    private static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS)
                .plusHours(EXPIRE_HOURS)
                .plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken createToken(String username, String role) {
        try {
            Date issuedAt = new Date();
            Date expiration = toExpireDate(issuedAt);

            String token = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setSubject(username)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiration)
                    .signWith(generateKey(), SignatureAlgorithm.HS256)
                    .claim("role", role)
                    .compact();

            return new JwtToken(token);

        } catch (Exception e) {
            log.error("Erro ao criar token JWT: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar token JWT", e);
        }
    }

    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(refactorToken(token))
                    .getBody();
        } catch (JwtException ex) {
            log.error("Token inválido: {}", ex.getMessage());
        }
        return null;
    }

    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public static boolean isTokenValid(String token) {
        try {
             Jwts.parser()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(refactorToken(token));
             return true;
        } catch (JwtException ex) {
            log.error("Token inválido: {}", ex.getMessage());
        }
        return false;
    }

    private static String refactorToken(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
