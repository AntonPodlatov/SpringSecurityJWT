package security.simple.example.config;

import security.simple.example.config.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String authHeaderStart = "Bearer ";
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith(authHeaderStart)) {
            jwt = authHeader.substring(authHeaderStart.length());

            try {
                username = jwtUtils.getUserName(jwt);
            } catch (ExpiredJwtException e) {
                log.debug("Token expired");
            } catch (SignatureException e) {
                log.debug("Bad signature");
            }
        }

        if (authHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username, null, jwtUtils.getPrivileges(jwt).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));

            SecurityContextHolder.getContext().setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }
}
