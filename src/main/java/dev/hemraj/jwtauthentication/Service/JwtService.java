package dev.hemraj.jwtauthentication.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${security.jwt.refreshtoken.expiration-time}")
    private long jwtRefreshExpiration;
    private Date expiresIn;
    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //actually calling method
    public String generateToken(UserDetails userDetails, String userID) {
        return generateToken(new HashMap<>(), userDetails,userID);
    }
//    public String generateRefreshToken(UserDetails userDetails, String userID){
//         Map<String, Object> extraClaims = new HashMap<>();
//         extraClaims.put("isRefreshToken", true);
//         return buildToken(extraClaims, userDetails, jwtRefreshExpiration, userID);
//    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails,String userID) {
        return buildToken(extraClaims, userDetails, jwtExpiration, userID);
    }

    public ZonedDateTime getExpirationTime() {
        ZonedDateTime gmtTime = expiresIn.toInstant().atZone(ZoneId.of("UTC"));
        return gmtTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String userID
    ) {
        String encodedId = Base64.getEncoder().encodeToString(userID.getBytes());
        extraClaims.put("userId", encodedId);

        expiresIn=new Date(System.currentTimeMillis() + expiration);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiresIn)
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
//        boolean isRefresh = isRefreshToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
//    public boolean isRefreshToken(String token) { return extractClaim(token, claims -> claims.get("isRefreshToken", Boolean.class));}
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));

    }
}
