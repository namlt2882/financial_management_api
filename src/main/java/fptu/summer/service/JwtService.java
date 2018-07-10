/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fptu.summer.config.jwt.JwtManager;
import fptu.summer.model.Role;
import fptu.summer.model.User;
import fptu.summer.model.enumeration.UserStatus;
import java.util.Date;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class JwtService {
    
    public static final String USERNAME = "username";
    public static final String ROLES = "roles";
    public static final String STATUS = "status";
    public static final String SECRET_KEY = "1jnd.8jm8.fnw9.ls0s.mc9s.2.f9s.20ds.d2r4.2sfy.bhg.7jgtf.vde";
    public static final int EXPIRE_TIME = 86400000;//1 day
    private final ObjectMapper om = new ObjectMapper();
    private final JwtManager manager = new JwtManager();
    
    public String generateTokenLogin(User user) {
        String token = null;
        try {
            // Create HMAC signer
            JWSSigner signer = new MACSigner(generateShareSecret());
            
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim(USERNAME, user.getUsername());
            builder.claim(ROLES, om.writeValueAsString(user.getRoles()));
            builder.claim(STATUS, user.getStatus());
            builder.expirationTime(generateExpirationDate());
            
            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            // Apply the HMAC protection
            signedJWT.sign(signer);

            // Serialize to compact form, produces something like
            // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
            token = signedJWT.serialize();
            manager.addToken(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
    
    private JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claims = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(generateShareSecret());
            if (signedJWT.verify(verifier)) {
                claims = signedJWT.getJWTClaimsSet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }
    
    private <T> T getObjectFromClaims(Class<T> className, String name, String token) {
        JWTClaimsSet claimsSet = getClaimsFromToken(token);
        return (T) claimsSet.getClaim(name);
    }
    
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRE_TIME);
    }
    
    private Date getExpirationDateFromToken(String token) {
        Date expiration = null;
        JWTClaimsSet claims = getClaimsFromToken(token);
        expiration = claims.getExpirationTime();
        return expiration;
    }
    
    public String getUsernameFromToken(String token) {
        String username = null;
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            username = claims.getStringClaim(USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }
    
    public int getStatusFromToken(String token) {
        int status = UserStatus.ENABLE.getStatus();
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            status = claims.getIntegerClaim(STATUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
    
    public Set<Role> getRolesFromToken(String token) {
        Set<Role> roles = null;
        JWTClaimsSet claims = getClaimsFromToken(token);
        try {
            String tmp = claims.getStringClaim(ROLES);
            TypeReference<Set<Role>> typeRef = new TypeReference<Set<Role>>() {
            };
            roles = om.readValue(tmp, typeRef);
//            roles = new HashSet<>(Arrays.asList(getObjectFromClaims(Role[].class, ROLES, token)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    public User getUserFromToken(String token) {
        User user = new User();
        user.setUsername(getUsernameFromToken(token));
        user.setRoles(getRolesFromToken(token));
        user.setStatus(getStatusFromToken(token));
        return user;
    }
    
    private byte[] generateShareSecret() {
        // Generate 256-bit (32-byte) shared secret
        byte[] sharedSecret = new byte[32];
        sharedSecret = SECRET_KEY.getBytes();
        return sharedSecret;
    }
    
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Boolean validateTokenLogin(String token) {
        if (token == null || token.trim().length() == 0) {
            return false;
        }
//        if (!manager.isTokenExisted(token)) {
//            return false;
//        }
        String username = getUsernameFromToken(token);
        
        if (username == null || username.isEmpty()) {
            return false;
        }
        if (isTokenExpired(token)) {
            return false;
        }
        return true;
    }
    
    public void invalidateToken(String token) {
        manager.invalidateToken(token);
    }
    
}
