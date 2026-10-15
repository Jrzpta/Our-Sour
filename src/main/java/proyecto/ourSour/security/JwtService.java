package proyecto.ourSour.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import proyecto.ourSour.model.User;

import org.springframework.beans.factory.annotation.Value;



@Service 
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value ("${jwt.secret}") String secret,
                      @Value ("${jwt.expiration-ms}") long expirationMs){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generate(User user){
        Date now = new Date();
        return  Jwts.builder()
        .subject(user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(now)
        .expiration(new Date(now.getTime()+ expirationMs))
        .signWith(key)
        .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
        .parseSignedClaims(token).getPayload();  
    }

}
