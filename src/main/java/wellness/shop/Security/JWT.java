package wellness.shop.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import wellness.shop.Models.Users.Enums.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
@Component
public class JWT {

    private static final String SECRET_KEY = "sdfsadfsadfasdfasdfasdfasdfasdfasdf";

    public static Claims decodeJwt(String jwt) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return claims;
    }

    public static String generateJwt(String userUUID, Role role) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + 120 * 60 * 1000);

        String jwt = Jwts.builder()
                .setIssuer("wellnessShop.lt")
                .setSubject("AuthenticationToken")
                .claim("userUUID", userUUID)
                .claim("role", role)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

}
