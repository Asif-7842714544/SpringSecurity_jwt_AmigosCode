package com.example.security.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY = "7IdzPicm3F38QhJIkGUBbFN6mbCaOauAJZCYugh0B/55HX0qDUY371YWNzk+4f243vIuAVrVy6vxl8leFwBqRIWVsiVcSK3amOi8MH7VMw2xpWTOyUw0rJs27oty7COLMShy7vK++hVK9EX6218djCSOYBSJ26cKxdBPSByEB95tPOM3DTvLFuTXDXIFJdnLu1/3tT7p7kCz5VVQ/jacW1SdBIBO5CdfoIk7TADyjOEhkbOE5ot2PNmNVRMIJcB4YMCitBwXe0Ophrw2KLODl7iMhh5Ya92uk1g8wPPgbgoPEn1gjkgyl6YGVXsWDEXIq1stRHuOdBaDO/6tAvvDjJ4xhOsDT0EUXPqhJYaGLO0=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims Claims = extractAllClaims(token);
        return claimResolver.apply(Claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 10000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
