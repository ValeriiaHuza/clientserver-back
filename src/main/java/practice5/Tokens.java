package practice5;

import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Tokens {

    private byte[] jwt_secret = "my-long-secret-key-for-jwt-very-very-very-long".getBytes(StandardCharsets.UTF_8);


    public String createJWT(String userLogin) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date now = new Date();

        Key signingKey = new SecretKeySpec(jwt_secret, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(userLogin)
                .setExpiration(new Date(now.getTime() + TimeUnit.HOURS.toMillis(10)))
                .signWith(signingKey,signatureAlgorithm);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public String parseJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(new SecretKeySpec(jwt_secret, SignatureAlgorithm.HS256.getJcaName()))
                .parseClaimsJws(jwt).getBody();

        return claims.getSubject();
//        System.out.println("ID: " + claims.getId());
//        System.out.println("Subject: " + claims.getSubject());
//        System.out.println("Issuer: " + claims.getIssuer());
//        System.out.println("Expiration: " + claims.getExpiration());
    }

}
