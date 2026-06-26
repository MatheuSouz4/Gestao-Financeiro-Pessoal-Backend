package com.example.loginauthapi.infra.security;

import com.example.loginauthapi.model.User;
import com.example.loginauthapi.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        // Log para acompanhar a extração
        if (token != null) {
            System.out.println("DEBUG (SecurityFilter) - Token recebido.");
        }

        if (token != null) {
            var login = tokenService.validateToken(token);

            if (login != null) {
                System.out.println("DEBUG (SecurityFilter) - Token validado com sucesso para o usuário: " + login);

                User user = userRepository.findByEmail(login)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco de dados."));

                // Atribui a permissão base (ROLE_USER)
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

                // Autentica a requisição para o Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("DEBUG (SecurityFilter) - Falha: Token inválido, expirado ou assinatura não confere.");
            }
        }

        // Segue o fluxo independentemente (se não autenticou, será barrado no Controller com 403)
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}