package dev.hemraj.kafka_001.service.authentication;

import dev.hemraj.kafka_001.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${spring.auth.jwt_secret}")
    private String secretKey;

    @Getter
    @Value("${spring.auth.jwt_expiration_time}")
    private long expirationTime;

    public String extractUserName(String jwtToken){
        return extractClaims(jwtToken, Claims::getSubject);
    }
    public <T> T extractClaims(String jwtToken, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String jwtToken){
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
    private Key getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims,userDetails,expirationTime);
    }
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        User user = (User) userDetails;
        extraClaims.put("id",user.getId());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(userDetails.getUsername())
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenExpired(String jwtToken){
        return extractClaims(jwtToken,Claims::getExpiration).before(new Date());
    }
    public boolean isTokenValid(String jwtToken, UserDetails userDetails){
        return extractClaims(jwtToken,Claims::getSubject).equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
    }


}
