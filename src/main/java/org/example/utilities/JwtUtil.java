package org.example.utilities;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.services.impl.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    private static final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode("JustASecretKeyHereJustASecretKeyHereJustASecretKeyHereJustASecretKeyHere"));
    private static final long EXPIRATION_MS = 3600000;  // 1hr

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserDetailsImpl) {
            claims.put("sub", userDetails.getUsername());
            claims.put("role", ((UserDetailsImpl) userDetails).getRole());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        String maskedToken = token.substring(0, 3) + "..." + token.substring(token.length() - 3);

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException e) {
            log.warn("JWT token '{}' expired: {}", maskedToken, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        }
        catch (Exception e) {
            log.warn("Invalid JWT token '{}': {}", maskedToken, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }
}

