package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.response.AuthenticationResponse;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.repository.MongoService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micrometer.common.util.StringUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private final MongoService mongoService;
  @NonFinal
  protected static final String SIGNER_KEY = "0Fo5yxReydyX2rCmgvtI5nELTqjS+o9XPuS4r9G7NzTZ2fSoMnxu8J40VDCLgc8A";

  public boolean userRegister(String username, String password) {
    if (StringUtils.isBlank(username)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    User user = new User();
    user.setUsername(username);
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    user.setPassword(passwordEncoder.encode(password));
    User savedUser = mongoService.save(user);
    return savedUser != null && savedUser.getUserId() != null;
  }

  public AuthenticationResponse userLogin(String username, String password) {
    if (StringUtils.isBlank(username)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Query query = new Query(Criteria.where("username").is(username));
    User existingUser = mongoService.findOne(query, User.class).orElse(null);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    boolean authicate = passwordEncoder.matches(password, existingUser.getPassword());
    if (!authicate){
      throw new AppException(ErrorCode.AUTHENTICATION);
    }

    var token = generateToken(username);

    return AuthenticationResponse.builder()
        .token(token)
        .authenticated(true)
        .build();

  }

  private String generateToken(String username){
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(username)
        .issuer("phuocbiu")
        .issueTime(new Date())
        .expirationTime(new Date(
            Instant.now().plus(31, ChronoUnit.DAYS).toEpochMilli()
        ))
        .claim("customClaim","Custom")
        .build();
    Payload payload = new Payload(jwtClaimsSet.toJSONObject());
    JWSObject jwsObject = new JWSObject(header, payload);
    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }


}
