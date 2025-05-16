
package com.tpSolar.halwaCityMarathon.security;

import com.tpSolar.halwaCityMarathon.util.JwtTokenGeneration;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenGeneration JwtTokenGeneration;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.tata.jwtSecret}")
    private String tataJwtSecret;

    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String header = request.getHeader("Authorization");

        logger.info("Request URI: ----{}",  uri);
        logger.info("Request Method: ------{}", method);
        logger.info("Authorization Header: -----{}" , header);

        // Allow public endpoints without token
        if (uri.equals("/halwaCityMarathon/login") || uri.equals("/halwaCityMarathon/register") || uri.equals("/halwaCityMarathon/tataLogin")) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        /*if (isLogin || isRegistrationPost) {
            logger.info("Allowing access to login or registration POST endpoint.");
            filterChain.doFilter(request, response);
            return;
        }*/

        if(uri.equals("/halwaCityMarathon/registrations")){
            secretKey = jwtSecret;
        }
        else { secretKey = tataJwtSecret;}
        // Now validate token for protected routes
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            logger.info("JWT token received: " + token); // Add logging to see the token being passed
            try {
                if (JwtTokenGeneration.validateToken(token, null, secretKey)) {
                    String username = JwtTokenGeneration.extractUsername(token, secretKey);
                    logger.info("Token validated for user: " + username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("Invalid JWT Token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                    return;
                }
            } catch (ExpiredJwtException ex) {
                logger.warn("Token expired.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired. Please log in again.");
                return;
            }
        } else {
            logger.warn("Missing or invalid JWT token.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT Token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
