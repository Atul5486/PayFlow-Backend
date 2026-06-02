package com.paypal.user_service.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JWTrequestFilter extends OncePerRequestFilter {

    private JWTutils jwTutils;

    public JWTrequestFilter(JWTutils jwTutils){
        this.jwTutils=jwTutils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader=request.getHeader("Authorization");
        String username=null;
        String jwt=null;
        String role=null;

        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            jwt=authorizationHeader.substring(7);

            if(jwt==null || jwt.isBlank()){
                filterChain.doFilter(request,response);
                return;
            }

            try {
//                Here checking username
                username = jwTutils.extractUsername(jwt);

//                Here getting role
                role = jwTutils.extractRole(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwTutils.validateToken(jwt, username)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        filterChain.doFilter(request, response);
                    }
                }
            }catch (Exception e){
                System.out.println("Exception Occur during filtering"+e.getMessage());
        }
    }else{
            filterChain.doFilter(request,response);
            return;
        }

}
}
