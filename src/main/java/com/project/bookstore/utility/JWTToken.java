package com.project.bookstore.utility;

import com.project.bookstore.dto.UserLoginDTO;
import com.project.bookstore.repository.UserRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTToken implements Serializable {
    @Autowired
    private UserLoginDTO loginData;

    @Autowired
    private UserRepo userRepo;
    private String SECRET_KEY = "login";

    public String generateToken(UserLoginDTO loginData) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("PhoneNumber", loginData.getPhoneNumber());
        claims.put("Password", loginData.getPassword());
        return Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    public UserLoginDTO decodeToken(String Token) {
        Map<String, Object> claims;
        claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(Token).getBody();
        loginData.setPhoneNumber((String) claims.get("PhoneNumber"));
        loginData.setPassword((String)(claims.get("Password")));
        return loginData;
    }

}
