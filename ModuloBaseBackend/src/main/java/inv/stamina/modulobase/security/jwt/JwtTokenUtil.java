/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Getter
@Setter
@Component
public class JwtTokenUtil implements Serializable {

    public static final String KEY_TOKEN = "Authorization";
    public static final String IP_CLIENT = "X-Forwarded-For";//Forwarded: for=192.0.2.60;proto=http;by=203.0.113.43
    public static final String FORM = "Source";//Forwarded: for=192.0.2.60;proto=http;by=203.0.113.43

    private static final long serialVersionUID = -2550185165626007488L;

    /*@Value("${jwt.timelife}")
    public String JWT_TOKEN_VALIDIT;*/
    
    //expresado en horas
    public Long TiempoValidacionToken=12l;

    @Value("${jwt.secret}")
    private String secret;

//retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return token;
        }

        return getClaimFromToken(token.substring(7), Claims::getSubject);
    }

    public String getRolNombreFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return token;
        }

        return (String) getAllClaimsFromToken(token.substring(7)).get("rol");
    }

//retrieve expiration date from jwt token
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }

//check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token.substring(7));
        return expiration.before(new Date());
    }

//generate token for user
    public String generateToken(String nombreUsuario, String rolName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rolName);
        return doGenerateToken(claims, nombreUsuario);
    }

//while creating the token -
//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
//2. Sign the JWT using the HS512 algorithm and secret key.
//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
//   compaction of the JWT to a URL-safe string 
// para que token expire en minutos seria   30min/60 *1000 *60*60
    private String doGenerateToken(Map<String, Object> claims, String subject) {
       return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (this.TiempoValidacionToken*1000) * 60 * 60 ))
//                .setExpiration(new Date(System.currentTimeMillis() + (700*1000) * 60 * 60 ))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8)).compact();
    }

}
