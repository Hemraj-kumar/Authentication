package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.ResponseDto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final UserDetailsService userDetailsService;
    public JwtService(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }
    public String extractUsername(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateRefreshToken(UserDetails userDetails, String userID){
        return buildToken(new HashMap<>(), userDetails, jwtRefreshExpiration, userID);
    }
    public String generateAccessToken(UserDetails userDetails, String userID) {
        return generateToken(new HashMap<>(), userDetails,userID);
    }

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
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
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
    public ResponseEntity<TokenResponse> refreshAccessToken(String refreshToken ){
        TokenResponse response = new TokenResponse();
        try {
            String userID = extractUserId(refreshToken);
            if (isTokenExpired(refreshToken)) {
                response.setRefreshToken("");
                response.setAccessToken("");
                response.setDescription("refresh token expired");
                response.setSuccess(false);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setExpiresAt(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(refreshToken));
            if (isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = generateAccessToken(userDetails, userID);
                response.setRefreshToken(refreshToken);
                response.setAccessToken(newAccessToken);
                response.setDescription("access token generated");
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK);
                response.setExpiresAt(extractExpiration(newAccessToken));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.setRefreshToken("");
                response.setAccessToken("");
                response.setDescription("Invalid Token");
                response.setSuccess(false);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setExpiresAt(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }catch (Exception err){
            log.error("Error in refreshing the access token : ", err);
        }
        return ResponseEntity.internalServerError().body(response);
    }
}

