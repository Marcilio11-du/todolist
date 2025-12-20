package com.example.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTasksAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            var servletPath = request.getServletPath();
            if(servletPath.equals("/tasks/")) {
                //Pregar autenticação (utilizador e senha)
                var authorization = request.getHeader("Authorization");
                System.out.println("Authorization");

                var authEncoded = authorization.substring("Basic".length()).trim();
                byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
                var authString = new String(authDecoded);
                String[] credentials = authString.split(":");
                String userName = credentials[0];
                String password = credentials[1];
                var user = this.userRepository.findByUserName(userName);
                if(user != null) {
                    //Validação da senha
                    var passwordToChek = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());
                    if(passwordToChek.verified)
                        filterChain.doFilter(request, response);
                    else
                        response.sendError(401);
                }else {
                    response.sendError(401);
                }
            } else
                filterChain.doFilter(request, response);
    }
}
