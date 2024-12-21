package wellness.shop.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wellness.shop.Models.Users.Enums.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
@Component
public class JWT {

    @Value("${jwt.key}")
    private String SECRET_KEY;
    @Value("${jwt.token.maxsize}")
    private int MAX_TOKEN_LENGTH;
    public Claims decodeJwt(String jwt) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return claims;
    }

    public String generateJwt(String userUUID, Role role) {

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

    public boolean checkRolesAndValidateToken(String JWToken, Role[] allowedRoles){

        if (JWToken == null || JWToken.length() > MAX_TOKEN_LENGTH) {
            return false;
        }

        if (StringUtils.startsWithIgnoreCase(JWToken, "Bearer ")) JWToken = JWToken.substring(7);
        else return false;

        try{
            Claims claims = decodeJwt(JWToken);
            String roleString = (String) claims.get("role");
            Role role = Role.valueOf(roleString);

            for(int i = 0 ; i < allowedRoles.length; i ++){
                if (role == allowedRoles[i]) return true;
            }
            return false;
        }
        catch (io.jsonwebtoken.security.SignatureException |
               io.jsonwebtoken.ExpiredJwtException |
               io.jsonwebtoken.MalformedJwtException |
               io.jsonwebtoken.UnsupportedJwtException |
               IllegalArgumentException e) {
            return false;
        }

    }

}
