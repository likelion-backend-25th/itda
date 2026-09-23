package net.likelion.bebc25.itda.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestAuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String clientIp = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            log.info("[HTTP AUDIT] {} {} | 상태코드: {} | 소요시간: {}ms | 접속IP: {}",
                    method, uri, status, duration, clientIp);
        }
    }
}