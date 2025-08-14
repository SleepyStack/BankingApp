package com.sleepystack.bankingapp.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String clientIp = request.getRemoteAddr();
        String uri = request.getRequestURI();
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> uri.startsWith("/auth/login") ? Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build() : uri.startsWith("/auth/register") ? Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(60))))
                .build() : Bucket.builder()
                .addLimit(Bandwidth.classic(15, Refill.greedy(15, Duration.ofMinutes(1))))
                .build());
        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            ((HttpServletResponse) res).setStatus(429);
            res.getWriter().write("Too Many Requests");// Too Many Requests
        }

    }
}
