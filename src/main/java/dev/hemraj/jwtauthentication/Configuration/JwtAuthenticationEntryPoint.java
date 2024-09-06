package dev.hemraj.jwtauthentication.Configuration;

import dev.hemraj.jwtauthentication.Service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private final JwtService jwtService;
    public JwtAuthenticationEntryPoint(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try{
            if(jwtService.isTokenExpired(request.getHeader(AUTHORIZATION).substring(7))){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token Expired");
            }else{
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
            }
        }catch (ExpiredJwtException exception){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //need to check , y its used?
            response.getWriter().write("Token Expired");
            response.getWriter().flush();
        }catch (Exception err){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
            log.error("Exception  : ",err);
        }
    }
}
