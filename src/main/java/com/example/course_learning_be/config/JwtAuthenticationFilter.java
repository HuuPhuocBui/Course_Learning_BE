package com.example.course_learning_be.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  @Value("${jwt.signer-key}")
  private String signerKey;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);

    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
      if (!signedJWT.verify(verifier)) {
        throw new SecurityException("Invalid JWT signature");
      }

      var claims = signedJWT.getJWTClaimsSet();
      String username = claims.getSubject();
      String role = claims.getStringClaim("role");
      List<String> purchasedCourses =
          (List<String>) claims.getClaim("purchasedCourseIds");

      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(username, purchasedCourses, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
