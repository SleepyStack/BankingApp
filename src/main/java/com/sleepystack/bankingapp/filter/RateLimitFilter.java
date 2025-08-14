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
        String key;
        if (uri.startsWith("/auth/login") || uri.startsWith("/home")) {
            key = clientIp + ":auth_or_home";
        } else if (uri.startsWith("/auth/register")) {
            key = clientIp + ":register";
        } else {
            key = clientIp + ":other";
        }

        Bucket bucket = buckets.computeIfAbsent(key, k -> {
            if (k.endsWith("auth_or_home")) {
                // 5 req/min
                return Bucket.builder().addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))).build();
            } else if (k.endsWith("register")) {
                // 5 req/hour
                return Bucket.builder().addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofHours(1)))).build();
            } else {
                // 15 req/min
                return Bucket.builder().addLimit(Bandwidth.classic(15, Refill.greedy(15, Duration.ofMinutes(1)))).build();
            }
        });
        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        }else {
                ((HttpServletResponse) res).setStatus(429);
                res.getWriter().write("Too Many Requests");
        }
    }
}
