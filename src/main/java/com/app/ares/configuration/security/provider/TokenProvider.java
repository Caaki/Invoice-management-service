package com.app.ares.configuration.security.provider;

import com.app.ares.domain.UserPrincipal;
import com.app.ares.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private final UserService userService;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    private static final String ARES ="ARES";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";
    private static final String AUTHORITIES = "authorities";
    private static final long ACESS_TOKEN_EXPIRATION_TIME = 1_800_000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;
    @Value("${jwt.secret}")
    private String secret;


    public String createAccessToken(UserPrincipal userPrincipal){
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(ARES)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(currentTimeMillis() + ACESS_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserPrincipal userPrincipal){
        return JWT.create()
                .withIssuer(ARES)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());

    }

    public boolean isTokenValid(String email, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNoneEmpty(email) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token, HttpServletRequest request){
        try {
            return getJWTVerifier().verify(token).getSubject();
        }catch (TokenExpiredException e){
            log.error("Error in getSubject() at TokenProvider");
            request.setAttribute("expiredMessage", e.getMessage());
            throw e;
        }catch (InvalidClaimException e){
            log.error("Error in getSubject() at TokenProvider");
            request.setAttribute("invalidClaim", e.getMessage());
            throw e;
        }catch (Exception e){
            log.error("Error in getSubject() at TokenProvider");
            throw e;
        }
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());

    }

    public Authentication getAuthentication(String email, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email),null,authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }


    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }


    private String[] getClaimsFromToken(String token) {

        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);

    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(ARES).build();
        }catch (JWTVerificationException e){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

}
